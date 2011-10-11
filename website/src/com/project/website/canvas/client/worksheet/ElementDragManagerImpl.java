package com.project.website.canvas.client.worksheet;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HumanInputEvent;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.client.handlers.RegistrationsManager;
import com.project.shared.client.utils.ElementUtils;
import com.project.shared.client.utils.EventUtils;
import com.project.shared.client.utils.widgets.WidgetUtils;
import com.project.shared.data.Point2D;
import com.project.website.canvas.client.worksheet.exceptions.InvalidDragPanelRelationshipException;
import com.project.website.canvas.client.worksheet.interfaces.ElementDragManager;
import com.project.website.canvas.client.worksheet.interfaces.MouseMoveOperationHandler;

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
            final Element targetElement, final Point2D referenceOffset, MouseMoveOperationHandler handler, int stopConditions)
    {
        return this.startMouseMoveOperation(targetElement, targetElement, referenceOffset, handler, stopConditions);
    }

    @Override
    public SimpleEvent.Handler<Void> startMouseMoveOperation(
            final Element targetElement, final Element referenceElem, final Point2D referenceOffset,
            final MouseMoveOperationHandler handler, int stopConditions)
    {
        final RegistrationsManager regs = new RegistrationsManager();
        if (false == setStopConditionHandlers(targetElement, referenceElem, handler, stopConditions, regs)) {
            throw new RuntimeException("Must specify at least one stop condition. The bitfield was: " + stopConditions);
        }
        ElementUtils.setTextSelectionEnabled(_container.getElement(), false);

        final MouseDragHandler dragHandler = new MouseDragHandler(
                EventUtils.getCurrentMousePos(), this._dragStartSensitivity);
        regs.add(dragHandler.addDragStartedHandler(new SimpleEvent.Handler<HumanInputEvent<?>>() {
            @Override public void onFire(HumanInputEvent<?> arg) {
                ElementUtils.addClassName(targetElement, _targetDragStyleName);
                _dragPanel.setVisible(true);
                handler.onStart();
                arg.preventDefault();
            }
        }));
        regs.add(dragHandler.addDragHandler(new SimpleEvent.Handler<HumanInputEvent<?>>() {
            @Override public void onFire(HumanInputEvent<?> arg) {
                handleMouseMove(referenceElem, referenceOffset, handler);
                arg.preventDefault();
            }
        }));

        regs.add(WidgetUtils.addMovementMoveHandler(_container, dragHandler));

        if (null != _stopOperationEvent) {
            regs.add(_stopOperationEvent.addHandler(new SimpleEvent.Handler<Void>() {
                @Override public void onFire(Void arg) {
                    stopMouseMoveOperation(targetElement, regs);
                    handler.onCancel();
                }
            }));
        }

        return new Handler<Void>() {
            @Override public void onFire(Void arg) {
                stopMouseMoveOperation(targetElement, regs);
            }
        };

    }

    private void operationEnded(final Element targetElement, final Element referenceElem, MouseMoveOperationHandler handler, RegistrationsManager regs)
    {
        stopMouseMoveOperation(targetElement, regs);
        handler.onStop(ElementUtils.getMousePositionRelativeToElement(referenceElem));
    }

    private boolean setStopConditionHandlers(final Element targetElement,
            final Element referenceElem, final MouseMoveOperationHandler handler,
            int stopConditions, final RegistrationsManager regs)
    {
        boolean stopConditionFound = false;

        if (0 != (stopConditions & StopCondition.STOP_CONDITION_MOVEMENT_STOP)) {
            stopConditionFound = true;
            regs.add(WidgetUtils.addMovementStopHandler(_container, new Handler<HumanInputEvent<?>>() {
                @Override public void onFire(HumanInputEvent<?> arg) {
                    operationEnded(targetElement, referenceElem, handler, regs);
                }}));
        }
        if (0 != (stopConditions & StopCondition.STOP_CONDITION_MOUSE_CLICK)) {
            stopConditionFound = true;
            regs.add(_container.addDomHandler(new ClickHandler() {
                @Override public void onClick(ClickEvent event) {
                    operationEnded(targetElement, referenceElem, handler, regs);
                }
            }, ClickEvent.getType()));
        }
        return stopConditionFound;
    }

    private void stopMouseMoveOperation(Element targetElement, RegistrationsManager regs)
    {
        ElementUtils.removeClassName(targetElement, this._targetDragStyleName);
        // TODO: restore the previous state of text selection enabled from before the mouse operation, instead of
        // forcing it to "true"
        ElementUtils.setTextSelectionEnabled(_container.getElement(), true);
        _dragPanel.setVisible(false);
        regs.clear();
    }

    private void handleMouseMove(Element referenceElem, Point2D referenceOffset, MouseMoveOperationHandler handler)
    {
        handler.onMouseMove(ElementUtils.getMousePositionRelativeToElement(referenceElem).minus(referenceOffset));
    }
}
