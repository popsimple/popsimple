package com.project.canvas.client.worksheet;

import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.data.Point2D;

public class MouseDragHandler implements MouseMoveHandler
{
    private MouseMoveHandler _currentHandler = null;
    private int _dragStartSensitivity = 0;
    private Point2D _initialPosition = null;

    private SimpleEvent<Void> _dragStartedEvent = new SimpleEvent<Void>();
    private SimpleEvent<MouseMoveEvent> _dragEvent = new SimpleEvent<MouseMoveEvent>();

    public MouseDragHandler(Point2D initialPosition, int dragStartSensitivity)
    {
        this._initialPosition = initialPosition;
        this._dragStartSensitivity = dragStartSensitivity;
        this._currentHandler = new MouseMoveHandler() {
            @Override
            public void onMouseMove(MouseMoveEvent event) {
                onInitialMouseMove(event);
            }
        };
    }

    public HandlerRegistration addDragStartedHandler(SimpleEvent.Handler<Void> handler)
    {
        return this._dragStartedEvent.addHandler(handler);
    }

    public HandlerRegistration addDragHandler(SimpleEvent.Handler<MouseMoveEvent> handler)
    {
        return this._dragEvent.addHandler(handler);
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
        this._currentHandler.onMouseMove(event);
    }

    private void onInitialMouseMove(MouseMoveEvent event)
    {
        if (false == this.shouldStartDrag(event))
        {
            return;
        }
        this._currentHandler = new MouseMoveHandler() {
            @Override
            public void onMouseMove(MouseMoveEvent event) {
                onActualMouseMove(event);
            }
        };
        this._dragStartedEvent.dispatch(null);
    }

    private boolean shouldStartDrag(MouseEvent<?> event)
    {
        if (0 >= this._dragStartSensitivity){
            return true;
        }
        if (null == this._initialPosition)
        {
            return true;
        }
        Point2D currentPosition = new Point2D(event.getClientX(), event.getClientY());
        if (currentPosition.minus(this._initialPosition).radius() > this._dragStartSensitivity)
        {
            return true;
        }
        return false;
    }

    private void onActualMouseMove(MouseMoveEvent event)
    {
        this._dragEvent.dispatch(event);
    }
}
