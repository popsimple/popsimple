package com.project.canvas.client.shared;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

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
		widget.addDomHandler(new KeyDownHandler(){
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == 27) {
					widget.setFocus(false);
				}
			}}, KeyDownEvent.getType());
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
		widget.addDomHandler(new MouseDownHandler(){
			@Override
			public void onMouseDown(MouseDownEvent event) {
				event.preventDefault();
			}}, MouseDownEvent.getType());
	}

}
