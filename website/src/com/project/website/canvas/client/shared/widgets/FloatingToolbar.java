package com.project.website.canvas.client.shared.widgets;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.handlers.RegistrationsManager;
import com.project.shared.client.utils.ElementUtils;
import com.project.shared.data.Point2D;
import com.project.shared.data.Rectangle;
import com.project.website.canvas.client.resources.CanvasResources;

public class FloatingToolbar extends FlowPanel
{
    private final RegistrationsManager registrationsManager = new RegistrationsManager();

    protected Widget _editedWidget = null;

    public FloatingToolbar()
    {
        this.addStyleName(CanvasResources.INSTANCE.main().floatingToolbar());
    }

    public void setEditedWidget(Widget widget)
    {
        if (this._editedWidget == widget)
        {
            return;
        }
        this._editedWidget = widget;
        this.setVisible(null != this._editedWidget);
        this.registrationsManager.clear();

        if (null == this._editedWidget)
        {
            return;
        }
        this.setRegistrations();
    }

    private void setRegistrations()
    {
        final FloatingToolbar that = this;
        this.registrationsManager.add(this._editedWidget.addDomHandler(new MouseDownHandler() {
            @Override public void onMouseDown(MouseDownEvent event) {
                that.updatePosition();
            }}, MouseDownEvent.getType()));
//        this.registrationsManager.add(this._editedWidget.addDomHandler(new MouseUpHandler() {
//            @Override public void onMouseUp(MouseUpEvent event) {
//                that.updatePosition();
//            }}, MouseUpEvent.getType()));
//        this.registrationsManager.add(this._editedWidget.addDomHandler(new KeyDownHandler() {
//            @Override public void onKeyDown(KeyDownEvent event) {
//                that.updatePosition();
//            }}, KeyDownEvent.getType()));
        this.registrationsManager.add(this._editedWidget.addDomHandler(new FocusHandler() {
            @Override public void onFocus(FocusEvent event) {
                that.updatePosition();
            }}, FocusEvent.getType()));
        this.registrationsManager.add(Event.addNativePreviewHandler(new NativePreviewHandler() {
            @Override
            public void onPreviewNativeEvent(NativePreviewEvent event)
            {
                String eventType = event.getNativeEvent().getType();
                if (eventType.equals(KeyDownEvent.getType().getName()))
                {
                    // Update the position after the element has handled the event.
                    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                        @Override public void execute() {
                            that.updatePosition();
                        }
                    });
                }
            }
        }));
    }

    public void updatePosition()
    {
        if (null == this._editedWidget) {
            return;
        }
        Element element = this._editedWidget.getElement();
        Rectangle elementRect = ElementUtils.getElementAbsoluteRectangle(element);
        Point2D targetPos = elementRect.getCenter();
        Point2D[] corners = elementRect.getCorners().asArray();
        for (int i = 0 ; i < corners.length; i++)
        {
            targetPos.setX(Math.min(targetPos.getX(), corners[i].getX()));
            targetPos.setY(Math.max(targetPos.getY(), corners[i].getY()));
        }
        ElementUtils.setElementPosition(this.getElement(), targetPos, 300);
    }
}