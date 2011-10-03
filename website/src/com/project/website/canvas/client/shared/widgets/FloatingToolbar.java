package com.project.website.canvas.client.shared.widgets;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.handlers.RegistrationsManager;
import com.project.shared.client.utils.ElementUtils;
import com.project.shared.client.utils.EventUtils;
import com.project.shared.client.utils.SchedulerUtils;
import com.project.shared.client.utils.WindowUtils;
import com.project.shared.data.Point2D;
import com.project.shared.data.Rectangle;
import com.project.website.canvas.client.resources.CanvasResources;

public class FloatingToolbar extends FlowPanel
{
    private static final int TOOLBAR_MARGIN = 10;

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
        this.updatePosition();
    }

    private void setRegistrations()
    {
        final FloatingToolbar that = this;
        this.registrationsManager.add(this._editedWidget.addDomHandler(new MouseDownHandler() {
            @Override public void onMouseDown(MouseDownEvent event) {
                that.updatePosition();
            }}, MouseDownEvent.getType()));
        this.registrationsManager.add(this._editedWidget.addDomHandler(new TouchStartHandler() {
            @Override public void onTouchStart(TouchStartEvent event) {
                that.updatePosition();
            }}, TouchStartEvent.getType()));
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
        this.registrationsManager.add(Window.addResizeHandler(new ResizeHandler() {
            @Override public void onResize(ResizeEvent event) {
                that.updatePosition();
            }
        }));
        this.registrationsManager.add(Event.addNativePreviewHandler(new NativePreviewHandler() {
            @Override
            public void onPreviewNativeEvent(NativePreviewEvent event)
            {
                if (EventUtils.nativePreviewEventTypeEquals(event, KeyDownEvent.getType())) {
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
        SchedulerUtils.OneTimeScheduler.get().scheduleDeferredOnce(new ScheduledCommand() {
            @Override public void execute() {
                actualUpdatePosition();
            }
        });
    }

    public void actualUpdatePosition()
    {
        if (null == this._editedWidget) {
            return;
        }
        Element element = this._editedWidget.getElement();
        Rectangle elementRect = ElementUtils.getElementAbsoluteRectangle(element);
        Point2D minCorner = elementRect.getCenter();
        Point2D maxCorner = elementRect.getCenter();
        Point2D[] corners = elementRect.getCorners().asArray();
        for (int i = 0 ; i < corners.length; i++)
        {
            minCorner = Point2D.min(minCorner, corners[i]);
            maxCorner = Point2D.max(maxCorner, corners[i]);
        }
        Point2D mySize = ElementUtils.getElementOffsetSize(this.getElement());
        Point2D windowSize = WindowUtils.getClientSize();
        Point2D maxToolbarPosInWindow = windowSize.minus(mySize).minus(new Point2D(TOOLBAR_MARGIN, TOOLBAR_MARGIN));

        Point2D targetPos = minCorner.minus(new Point2D(0, mySize.getY() + TOOLBAR_MARGIN));

        Point2D fixedTargetPos = targetPos.limitTo(Point2D.zero, maxToolbarPosInWindow);
        if (fixedTargetPos.getY() > minCorner.getY())
        {
            // The toolbar will be inside the element, move it to below it instead of trying to fit it above.
            fixedTargetPos.setY(maxCorner.getY() + TOOLBAR_MARGIN);
            fixedTargetPos = fixedTargetPos.limitTo(Point2D.zero, maxToolbarPosInWindow);
        }

        ElementUtils.setElementCSSPosition(this.getElement(), fixedTargetPos, 300);
    }
}