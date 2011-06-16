package com.project.canvas.client.worksheet;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.DOM;
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
    private String _targetDragStyleName = "";
    private SimpleEvent<Void> _stopOperationEvent;
    private int _startDragPixelOffset = 5;
    private boolean _moveStarted = false;

    public ElementDragManagerImpl(Widget container, Widget dragPanel,
            SimpleEvent<Void> stopOperationEvent)
    {
        this(container, dragPanel, 5, stopOperationEvent);
    }

    public ElementDragManagerImpl(Widget container, Widget dragPanel,
            int startDragPixelOffset, SimpleEvent<Void> stopOperationEvent)
    {
        this(container, dragPanel, "", startDragPixelOffset, stopOperationEvent);
    }

    public ElementDragManagerImpl(Widget container, Widget dragPanel,
            String targetDragStyleName, SimpleEvent<Void> stopOperationEvent)
    {
        this(container, dragPanel, targetDragStyleName, 5, stopOperationEvent);
    }

    public ElementDragManagerImpl(Widget container, Widget dragPanel,
            String targetDragStyleName, int startDragPixelOffet, SimpleEvent<Void> stopOperationEvent)
    {
        this._container = container;
        this._dragPanel = dragPanel;
        this._targetDragStyleName = targetDragStyleName;
        this._stopOperationEvent = stopOperationEvent;
    }

    @Override
    public SimpleEvent.Handler<Void> startMouseMoveOperation(MouseEvent<?> startEvent,
            final Element targetElement, final Point2D referenceOffset,
            final SimpleEvent.Handler<Point2D> moveHandler, final Handler<Point2D> stopHandler,
            final SimpleEvent.Handler<Void> cancelHandler, int stopConditions)
    {
        return this.startMouseMoveOperation(startEvent, targetElement, targetElement,
                referenceOffset, moveHandler, stopHandler, cancelHandler, stopConditions);
    }

    @Override
    public SimpleEvent.Handler<Void> startMouseMoveOperation(MouseEvent<?> startEvent,
            final Element targetElement, final Element referenceElem, final Point2D referenceOffset,
            final SimpleEvent.Handler<Point2D> moveHandler, final Handler<Point2D> stopHandler,
            final SimpleEvent.Handler<Void> cancelHandler, int stopConditions)
    {
        final RegistrationsManager regs = new RegistrationsManager();
        if (false == setStopConditionHandlers(targetElement, referenceElem, stopHandler, stopConditions, regs)) {
            throw new RuntimeException("Must specify at least one stop condition. The bitfield was: " + stopConditions);
        }

        final Point2D initialPosition;
        if (null != startEvent)
        {
            initialPosition = new Point2D(startEvent.getClientX(), startEvent.getClientY());
        }
        else
        {
            initialPosition = new Point2D(0, 0);
        }

        NativeUtils.disableTextSelectInternal(_container.getElement(), true);

        regs.add(_dragPanel.addDomHandler(new MouseMoveHandler() {
            @Override
            public void onMouseMove(MouseMoveEvent event)
            {
                if (false == _moveStarted)
                {
                    Point2D currentPosition = new Point2D(event.getClientX(), event.getClientY());
                    if (currentPosition.minus(initialPosition).radius() < _startDragPixelOffset)
                    {
                        return;
                    }
                    DOM.releaseCapture(_dragPanel.getElement());
                    ElementUtils.addStyleName(targetElement, _targetDragStyleName);
                    _moveStarted = true;
                    _dragPanel.setVisible(true);
                    return;
                }
                Point2D pos = ElementUtils.relativePosition(event, referenceElem);
                handleMouseMove(referenceElem, referenceOffset, moveHandler, pos);
                event.stopPropagation();
            }}, MouseMoveEvent.getType()));

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

        DOM.setCapture(this._dragPanel.getElement());

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
            regs.add(_dragPanel.addDomHandler(new MouseUpHandler() {
                @Override
                public void onMouseUp(MouseUpEvent event)
                {
                    operationEnded(targetElement, referenceElem, floatingWidgetStop, regs, event);
                }
            }, MouseUpEvent.getType()));
        }
        if (0 != (stopConditions & StopCondition.STOP_CONDITION_MOUSE_CLICK)) {
            stopConditionFound = true;
            regs.add(_dragPanel.addDomHandler(new ClickHandler() {
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
        DOM.releaseCapture(_dragPanel.getElement());
        _dragPanel.setVisible(false);
        this._moveStarted = false;
        regs.clear();
    }

    private void handleMouseMove(final Element referenceElem, final Point2D referenceOffset,
            final SimpleEvent.Handler<Point2D> moveHandler, Point2D pos)
    {
        moveHandler.onFire(pos.minus(referenceOffset));
    }
}
