package com.project.website.canvas.client.canvastools.base;

import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.data.Point2D;

public interface ICanvasToolEvents
{
    HandlerRegistration addKillRequestEventHandler(Handler<Void> handler);

    // tool wants to be dragged around with the mouse
    HandlerRegistration addMoveStartEventHandler(Handler<MouseEvent<?>> handler);

    // Tool wants to move an offset
    HandlerRegistration addSelfMoveRequestEventHandler(Handler<Point2D> handler);

    HandlerRegistration addLoadStartedEventHandler(Handler<Void> handler);

    HandlerRegistration addLoadEndedEventHandler(Handler<Void> handler);

    HandlerRegistration addFocusHandler(FocusHandler handler);

    HandlerRegistration addBlurHandler(BlurHandler handler);
}
