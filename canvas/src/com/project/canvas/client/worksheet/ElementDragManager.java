package com.project.canvas.client.worksheet;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.project.canvas.client.shared.ElementUtils;
import com.project.canvas.client.shared.NativeUtils;
import com.project.canvas.client.shared.RegistrationsManager;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.client.shared.events.SimpleEvent.Handler;
import com.project.canvas.shared.data.Point2D;

public class ElementDragManager
{
    public class StopCondition {
        public static final int STOP_CONDITION_MOUSE_UP = 1;
        public static final int STOP_CONDITION_MOUSE_CLICK = 2;
    }

    private Widget _container;
    private Widget _dragPanel;
    private SimpleEvent<Void> _stopOperationEvent;

    public ElementDragManager(Widget container, Widget dragPanel, SimpleEvent<Void> stopOperationEvent)
    {
        this._container = container;
        this._dragPanel = dragPanel;
        this._stopOperationEvent = stopOperationEvent;
    }

    /**
     * Starts a mouse move/drag operation. Takes care of placing a drag panel on top of all others to capture events, etc. 
     * @param referenceElem The element relative to which the position will be calculated
     * @param referenceOffset An additional constant offset added to the position
     * @param moveHandler Called when the mouse moves
     * @param floatingWidgetStop Called when the operation ends
     * @param cancelHandler Called when the operation is cancelled
     * @param stopConditions Conditions for stopping the operation - a bit field of StopCondition values
     * @return
     */
    protected RegistrationsManager startMouseMoveOperation(final Element referenceElem, final Point2D referenceOffset,
            final SimpleEvent.Handler<Point2D> moveHandler, final Handler<Point2D> floatingWidgetStop,
            final SimpleEvent.Handler<Void> cancelHandler, int stopConditions)
    {
        final RegistrationsManager regs = new RegistrationsManager();

        NativeUtils.disableTextSelectInternal(_container.getElement(), true);
        regs.add(_dragPanel.addDomHandler(new MouseMoveHandler() {
            @Override
            public void onMouseMove(MouseMoveEvent event)
            {
                Point2D pos = ElementUtils.relativePosition(event, referenceElem);
                moveHandler.onFire(pos.minus(referenceOffset));
                event.stopPropagation();
            }
        }, MouseMoveEvent.getType()));
        if (0 != (stopConditions & StopCondition.STOP_CONDITION_MOUSE_UP)) {
            regs.add(_dragPanel.addDomHandler(new MouseUpHandler() {
                @Override
                public void onMouseUp(MouseUpEvent event)
                {
                    operationEnded(referenceElem, floatingWidgetStop, regs, event);
                }
            }, MouseUpEvent.getType()));
        }
        if (0 != (stopConditions & StopCondition.STOP_CONDITION_MOUSE_CLICK)) {
            regs.add(_dragPanel.addDomHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event)
                {
                    operationEnded(referenceElem, floatingWidgetStop, regs, event);
                }
            }, ClickEvent.getType()));
        }
        if (null != _stopOperationEvent) {
            regs.add(_stopOperationEvent.addHandler(new SimpleEvent.Handler<Void>() {
                @Override
                public void onFire(Void arg)
                {
                    stopMouseMoveOperation(regs);
                    cancelHandler.onFire(null);
                }
            }));
        }
        Event.setCapture(_dragPanel.getElement());
        _dragPanel.setVisible(true);
        return regs;
    }

    protected void stopMouseMoveOperation(final RegistrationsManager regs)
    {
        NativeUtils.disableTextSelectInternal(_container.getElement(), false);
        Event.releaseCapture(_dragPanel.getElement());
        regs.clear();
        _dragPanel.setVisible(false);
    }

    public void operationEnded(final Element referenceElem, final Handler<Point2D> floatingWidgetStop,
            final RegistrationsManager regs, MouseEvent<?> event)
    {
        stopMouseMoveOperation(regs);
        if (null != floatingWidgetStop) {
            floatingWidgetStop.onFire(ElementUtils.relativePosition(event, referenceElem));
        }
    }

}
