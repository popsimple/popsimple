package com.project.shared.client.utils;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.data.Point2D;

public class WidgetUtils {

    public static void stopClickPropagation(Widget widget) {
        widget.addDomHandler(new ClickHandler() {
            public void onClick(ClickEvent arg0) {
                arg0.stopPropagation();
            }
        }, ClickEvent.getType());
        widget.addDomHandler(new MouseDownHandler() {
            public void onMouseDown(MouseDownEvent arg0) {
                arg0.stopPropagation();
            }
        }, MouseDownEvent.getType());
    }

    public static <T extends Widget & Focusable> void addEscapeUnfocusesHandler(final T widget) {
        widget.addDomHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event) {
                if (event.getNativeKeyCode() == 27) {
                    widget.setFocus(false);
                }
            }
        }, KeyDownEvent.getType());
    }

    public static HandlerRegistration stopMouseMovePropagation(Widget widget) {
        return widget.addDomHandler(new MouseMoveHandler() {
            @Override
            public void onMouseMove(MouseMoveEvent event) {
                event.stopPropagation();
            }
        }, MouseMoveEvent.getType());
    }

    public static void disableDrag(Widget widget) {
        widget.addDomHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                event.preventDefault();
            }
        }, MouseDownEvent.getType());
    }

	public static void setWidgetSize(Widget widget, Point2D editSize) {
		widget.setWidth(editSize.getX() + "px");
		widget.setHeight(editSize.getY() + "px");
	}

	public static void SetBackgroundImageAsync(final Widget widget,
            String imageUrl, String errorImageUrl, final boolean autoSize, final String loadingStyleName)
    {
	    WidgetUtils.SetBackgroundImageAsync(widget, imageUrl, errorImageUrl, autoSize, loadingStyleName,
	            HandlerUtils.<Void>emptyHandler(), HandlerUtils.<Void>emptyHandler());
    }

	public static void SetBackgroundImageAsync(final Widget widget, String imageUrl, String errorImageUrl,
	        final boolean autoSize, final String loadingStyleName,
	        final SimpleEvent.Handler<Void> loadHandler, final SimpleEvent.Handler<Void> errorHandler)
    {
	    widget.getElement().getStyle().clearBackgroundImage();
	    widget.addStyleName(loadingStyleName);
	    ElementUtils.SetBackgroundImageAsync(widget.getElement(), imageUrl, errorImageUrl, autoSize,
            new SimpleEvent.Handler<Void>() {
                @Override
                public void onFire(Void arg) {
                    widget.removeStyleName(loadingStyleName);
                    loadHandler.onFire(null);
                }},
            new SimpleEvent.Handler<Void>() {
                @Override
                public void onFire(Void arg) {
                    widget.removeStyleName(loadingStyleName);
                    errorHandler.onFire(null);
                }});
    }
}
