package com.project.website.canvas.client.worksheet;

import com.google.gwt.event.dom.client.HumanInputEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.utils.EventUtils;
import com.project.shared.data.Point2D;

public class MouseDragHandler implements SimpleEvent.Handler<HumanInputEvent<?>>
{
    private SimpleEvent.Handler<HumanInputEvent<?>> _currentHandler = null;
    private int _dragStartSensitivity = 0;
    private Point2D _initialPosition = null;

    private SimpleEvent<HumanInputEvent<?>> _dragStartedEvent = new SimpleEvent<HumanInputEvent<?>>();
    private SimpleEvent<HumanInputEvent<?>> _dragEvent = new SimpleEvent<HumanInputEvent<?>>();

    public MouseDragHandler(Point2D initialPosition, int dragStartSensitivity)
    {
        this._initialPosition = initialPosition;
        this._dragStartSensitivity = dragStartSensitivity;
        this._currentHandler = new SimpleEvent.Handler<HumanInputEvent<?>>() {
            @Override public void onFire(HumanInputEvent<?> arg) {
                onInitialMouseMove(arg);
            }};
    }

    public HandlerRegistration addDragStartedHandler(SimpleEvent.Handler<HumanInputEvent<?>> handler)
    {
        return this._dragStartedEvent.addHandler(handler);
    }

    public HandlerRegistration addDragHandler(SimpleEvent.Handler<HumanInputEvent<?>> handler)
    {
        return this._dragEvent.addHandler(handler);
    }

    private void onInitialMouseMove(HumanInputEvent<?> arg)
    {
        if (false == this.shouldStartDrag())
        {
            return;
        }
        this._currentHandler = new SimpleEvent.Handler<HumanInputEvent<?>>() {
            @Override public void onFire(HumanInputEvent<?> arg) {
                onActualMouseMove(arg);
            }};
        this._dragStartedEvent.dispatch(arg);
    }

    private boolean shouldStartDrag()
    {
        if (0 >= this._dragStartSensitivity){
            return true;
        }
        if (null == this._initialPosition)
        {
            return true;
        }
        Point2D currentPosition = EventUtils.getCurrentMousePos();
        if (currentPosition.minus(this._initialPosition).radius() > this._dragStartSensitivity)
        {
            return true;
        }
        return false;
    }

    private void onActualMouseMove(HumanInputEvent<?> event)
    {
        this._dragEvent.dispatch(event);
    }

    @Override
    public void onFire(HumanInputEvent<?> arg)
    {
        this._currentHandler.onFire(arg);
    }
}
