package com.project.website.canvas.client.canvastools.base;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.data.Point2D;
import com.project.website.canvas.client.canvastools.base.eventargs.LoadStartedEventArgs;
import com.project.website.canvas.client.canvastools.base.interfaces.ICanvasToolEvents;

public class CanvasToolEvents implements ICanvasToolEvents
{
    //#region Events

    private final SimpleEvent<Void> _killRequestEvent = new SimpleEvent<Void>();
    private final SimpleEvent<MouseEvent<?>> _moveStartRequestEvent = new SimpleEvent<MouseEvent<?>>();
    private SimpleEvent<Point2D> _selfMoveRequestEvent = new SimpleEvent<Point2D>();
    private final SimpleEvent<LoadStartedEventArgs> _loadStartedEvent = new SimpleEvent<LoadStartedEventArgs>();
    private final SimpleEvent<Void> _loadEndedEvent = new SimpleEvent<Void>();

    private Widget _domDispatcher = null;

    //#endregion

    public CanvasToolEvents(Widget domDispatcher)
    {
        this._domDispatcher = domDispatcher;
    }

    @Override
    public HandlerRegistration addKillRequestEventHandler(Handler<Void> handler) {
        return this._killRequestEvent.addHandler(handler);
    }

    public void dispatchKillRequestEvent()
    {
        this._killRequestEvent.dispatch(null);
    }

    @Override
    public HandlerRegistration addMoveStartEventHandler(Handler<MouseEvent<?>> handler) {
        return this._moveStartRequestEvent.addHandler(handler);
    }

    public void dispatchMoveStartRequestEvent(MouseEvent<?> mouseEvent)
    {
        this._moveStartRequestEvent.dispatch(mouseEvent);
    }

    @Override
    public HandlerRegistration addSelfMoveRequestEventHandler(Handler<Point2D> handler) {
        return this._selfMoveRequestEvent.addHandler(handler);
    }

    public void dispatchSelfMoveRequestEvent(Point2D targetPoint)
    {
        this._selfMoveRequestEvent.dispatch(targetPoint);
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        return this._domDispatcher.addDomHandler(handler, FocusEvent.getType());
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler handler) {
        return this._domDispatcher.addDomHandler(handler, BlurEvent.getType());
    }

    @Override
    public HandlerRegistration addLoadStartedEventHandler(Handler<LoadStartedEventArgs> handler) {
        return this._loadStartedEvent.addHandler(handler);
    }

    public void dispatchLoadStartedEvent()
    {
        this._loadStartedEvent.dispatch(new LoadStartedEventArgs());
    }

    public void dispatchLoadStartedEvent(LoadStartedEventArgs args)
    {
        this._loadStartedEvent.dispatch(args);
    }

    @Override
    public HandlerRegistration addLoadEndedEventHandler(Handler<Void> handler) {
        return this._loadEndedEvent.addHandler(handler);
    }

    public void dispatchLoadEndedEvent()
    {
        this._loadEndedEvent.dispatch(null);
    }
}
