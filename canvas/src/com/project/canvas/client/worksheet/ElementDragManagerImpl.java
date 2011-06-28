package com.project.canvas.client.worksheet;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.project.canvas.client.worksheet.exceptions.InvalidDragPanelRelationshipException;
import com.project.canvas.client.worksheet.interfaces.ElementDragManager;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.client.handlers.RegistrationsManager;
import com.project.shared.client.utils.ElementUtils;
import com.project.shared.client.utils.NativeUtils;
import com.project.shared.data.Point2D;
import com.project.shared.utils.EventUtils;

public class ElementDragManagerImpl implements ElementDragManager
{
    public final static int DEFAULT_DRAG_START_SENSITIVITY = 5;

    private Widget _container;
    private Widget _dragPanel;
    private String _targetDragStyleName = "";
    private SimpleEvent<Void> _stopOperationEvent;
    private int _dragStartSensitivity = DEFAULT_DRAG_START_SENSITIVITY;

    public ElementDragManagerImpl(Widget container, Widget dragPanel,
            SimpleEvent<Void> stopOperationEvent)
    {
        this(container, dragPanel, 5, stopOperationEvent);
    }

    public ElementDragManagerImpl(Widget container, Widget dragPanel,
            int dragStartSensitivity, SimpleEvent<Void> stopOperationEvent)
    {
        this(container, dragPanel, "", dragStartSensitivity, stopOperationEvent);
    }

    public ElementDragManagerImpl(Widget container, Widget dragPanel,
            String targetDragStyleName, SimpleEvent<Void> stopOperationEvent)
    {
        this(container, dragPanel, targetDragStyleName, 5, stopOperationEvent);
    }

    public ElementDragManagerImpl(Widget container, Widget dragPanel,
            String targetDragStyleName, int dragStartSensitivity, SimpleEvent<Void> stopOperationEvent)
    {
        this.validateDragPanel(container, dragPanel);
        this._container = container;
        this._dragPanel = dragPanel;
        this._targetDragStyleName = targetDragStyleName;
        this._stopOperationEvent = stopOperationEvent;
        this._dragStartSensitivity = dragStartSensitivity;
    }

    protected void validateDragPanel(Widget container, Widget dragPanel)
    {
        Element containerElement = container.getElement();
        Element dragPanelElement = dragPanel.getElement();
        if (containerElement == dragPanelElement)
        {
            throw new InvalidDragPanelRelationshipException();
        }
        if (false == containerElement.isOrHasChild(dragPanelElement))
        {
            throw new InvalidDragPanelRelationshipException();
        }
    }

    @Override
    public SimpleEvent.Handler<Void> startMouseMoveOperation(
            final Element targetElement, final Point2D referenceOffset,
            final SimpleEvent.Handler<Point2D> moveHandler, final Handler<Point2D> stopHandler,
            final SimpleEvent.Handler<Void> cancelHandler, int stopConditions)
    {
        return this.startMouseMoveOperation(targetElement, targetElement,
                referenceOffset, moveHandler, stopHandler, cancelHandler, stopConditions);
    }

    @Override
    public SimpleEvent.Handler<Void> startMouseMoveOperation(
            final Element targetElement, final Element referenceElem, final Point2D referenceOffset,
            final SimpleEvent.Handler<Point2D> moveHandler, final Handler<Point2D> stopHandler,
            final SimpleEvent.Handler<Void> cancelHandler, int stopConditions)
    {
        final RegistrationsManager regs = new RegistrationsManager();
        if (false == setStopConditionHandlers(targetElement, referenceElem, stopHandler, stopConditions, regs)) {
            throw new RuntimeException("Must specify at least one stop condition. The bitfield was: " + stopConditions);
        }

        NativeUtils.disableTextSelectInternal(_container.getElement(), true);

        MouseDragHandler dragHandler = new MouseDragHandler(
                EventUtils.getCurrentLocation(), this._dragStartSensitivity);
        regs.add(dragHandler.addDragStartedHandler(new SimpleEvent.Handler<Void>() {
            @Override
            public void onFire(Void arg) {
                ElementUtils.addStyleName(targetElement, _targetDragStyleName);
                _dragPanel.setVisible(true);
            }
        }));
        regs.add(dragHandler.addDragHandler(new SimpleEvent.Handler<MouseMoveEvent>() {
            @Override
            public void onFire(MouseMoveEvent arg) {
                Point2D pos = ElementUtils.relativePosition(arg, referenceElem);
                handleMouseMove(referenceElem, referenceOffset, moveHandler, pos);
            }
        }));

        regs.add(_container.addDomHandler(dragHandler, MouseMoveEvent.getType()));

        if (null != _stopOperationEvent) {
            regs.add(_stopOperationEvent.addHandler(new SimpleEvent.Handler<Void>() {
                @Override
                public void onFire(Void arg)
                {
                    stopMouseMoveOperation(targetElement, regs);
                    if (null != cancelHandler) {
                        cancelHandler.onFire(null);
                    }
                }
            }));
        }

        return new Handler<Void>() {
            @Override
            public void onFire(Void arg)
            {
                stopMouseMoveOperation(targetElement, regs);
            }
        };

    }

    private void operationEnded(final Element targetElement, final Element referenceElem,
        final Handler<Point2D> floatingWidgetStop, RegistrationsManager regs, MouseEvent<?> event)
    {
        stopMouseMoveOperation(targetElement, regs);
        if (null != floatingWidgetStop) {
            floatingWidgetStop.onFire(ElementUtils.relativePosition(event, referenceElem));
        }
    }

    private boolean setStopConditionHandlers(final Element targetElement,
            final Element referenceElem, final Handler<Point2D> floatingWidgetStop,
            int stopConditions, final RegistrationsManager regs)
    {
        boolean stopConditionFound = false;

        if (0 != (stopConditions & StopCondition.STOP_CONDITION_MOUSE_UP)) {
            stopConditionFound = true;
            regs.add(_container.addDomHandler(new MouseUpHandler() {
                @Override
                public void onMouseUp(MouseUpEvent event)
                {
                    operationEnded(targetElement, referenceElem, floatingWidgetStop, regs, event);
                }
            }, MouseUpEvent.getType()));
        }
        if (0 != (stopConditions & StopCondition.STOP_CONDITION_MOUSE_CLICK)) {
            stopConditionFound = true;
            regs.add(_container.addDomHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event)
                {
                    operationEnded(targetElement, referenceElem, floatingWidgetStop, regs, event);
                }
            }, ClickEvent.getType()));
        }
        return stopConditionFound;
    }

    private void stopMouseMoveOperation(Element targetElement, RegistrationsManager regs)
    {
        ElementUtils.removeStyleName(targetElement, this._targetDragStyleName);
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
