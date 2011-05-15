package com.project.canvas.client.canvastools.base;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.ui.Widget;

public abstract class CanvasToolCommon {
	
	public static void initCanvasToolWidget(Widget widget) {
		stopClickPropagation(widget);
	}

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
	
}
