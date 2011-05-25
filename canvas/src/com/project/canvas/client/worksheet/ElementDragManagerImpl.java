package com.project.canvas.client.worksheet;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.Widget;
import com.project.canvas.client.shared.ElementUtils;
import com.project.canvas.client.shared.NativeUtils;
import com.project.canvas.client.shared.RegistrationsManager;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.client.shared.events.SimpleEvent.Handler;
import com.project.canvas.client.worksheet.interfaces.ElementDragManager;
import com.project.canvas.shared.data.Point2D;

public class ElementDragManagerImpl implements ElementDragManager
{
    private Widget _container;
    private Widget _dragPanel;
    private SimpleEvent<Void> _stopOperationEvent;

    public ElementDragManagerImpl(Widget container, Widget dragPanel, SimpleEvent<Void> stopOperationEvent)
    {
        this._container = container;
        this._dragPanel = dragPanel;
        this._stopOperationEvent = stopOperationEvent;
    }

    @Override
    public SimpleEvent.Handler<Void> startMouseMoveOperation(final Element referenceElem,
            final Point2D referenceOffset, final SimpleEvent.Handler<Point2D> moveHandler,
            final Handler<Point2D> floatingWidgetStop, final SimpleEvent.Handler<Void> cancelHandler, 
            int stopConditions)
    {
        final RegistrationsManager regs = new RegistrationsManager();

        NativeUtils.disableTextSelectInternal(_container.getElement(), true);
        regs.add(_dragPanel.addDomHandler(new MouseMoveHandler() {
            @Override
            public void onMouseMove(MouseMoveEvent event)
            {
                Point2D pos = ElementUtils.relativePosition(event, referenceElem);
                handleMouseMove(referenceElem, referenceOffset, moveHandler, pos);
                event.stopPropagation();
            }
        }, MouseMoveEvent.getType()));

        if (false == setStopConditionHandlers(referenceElem, floatingWidgetStop, stopConditions, regs)) {
            throw new RuntimeException("Must specify at least one stop condition. The bitfield was: " + stopConditions);
        }

        if (null != _stopOperationEvent) {
            regs.add(_stopOperationEvent.addHandler(new SimpleEvent.Handler<Void>() {
                @Override
                public void onFire(Void arg)
                {
                    stopMouseMoveOperation(regs);
                    if (null != cancelHandler) {
                        cancelHandler.onFire(null);
                    }
                }
            }));
        }
        
        _dragPanel.setVisible(true);
        
        return new Handler<Void>() {
            @Override
            public void onFire(Void arg)
            {
                stopMouseMoveOperation(regs);
            }
        };
        
    }

    private void operationEnded(final Element referenceElem, final Handler<Point2D> floatingWidgetStop,
            final RegistrationsManager regs, MouseEvent<?> event)
    {
        stopMouseMoveOperation(regs);
        if (null != floatingWidgetStop) {
            floatingWidgetStop.onFire(ElementUtils.relativePosition(event, referenceElem));
        }
    }

    private boolean setStopConditionHandlers(final Element referenceElem, final Handler<Point2D> floatingWidgetStop,
            int stopConditions, final RegistrationsManager regs)
    {
        boolean stopConditionFound = false;

        if (0 != (stopConditions & StopCondition.STOP_CONDITION_MOUSE_UP)) {
            stopConditionFound = true;
            regs.add(_dragPanel.addDomHandler(new MouseUpHandler() {
                @Override
                public void onMouseUp(MouseUpEvent event)
                {
                    operationEnded(referenceElem, floatingWidgetStop, regs, event);
                }
            }, MouseUpEvent.getType()));
        }
        if (0 != (stopConditions & StopCondition.STOP_CONDITION_MOUSE_CLICK)) {
            stopConditionFound = true;
            regs.add(_dragPanel.addDomHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event)
                {
                    operationEnded(referenceElem, floatingWidgetStop, regs, event);
                }
            }, ClickEvent.getType()));
        }
        return stopConditionFound;
    }

    private void stopMouseMoveOperation(final RegistrationsManager regs)
    {
        NativeUtils.disableTextSelectInternal(_container.getElement(), false);
        _dragPanel.setVisible(false);
        regs.clear();
    }

    private void handleMouseMove(final Element referenceElem, final Point2D referenceOffset,
            final SimpleEvent.Handler<Point2D> moveHandler, Point2D pos)
    {
        moveHandler.onFire(pos.minus(referenceOffset));
    }

}
