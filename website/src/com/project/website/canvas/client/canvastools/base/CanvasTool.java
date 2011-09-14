package com.project.website.canvas.client.canvastools.base;

import com.google.gwt.event.dom.client.HasBlurHandlers;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.data.Point2D;
import com.project.website.canvas.shared.data.ElementData;

// TODO change getvalue to updateValue to reflect the fact that it mutates the instance of data that was given in the setValue?
public interface CanvasTool<T extends ElementData> extends IsWidget, TakesValue<T>, HasFocusHandlers, HasBlurHandlers {

	public enum ResizeMode {
		BOTH,
		RELATIVE,  // keeps aspect ratio
		WIDTH_ONLY,
		HEIGHT_ONLY,
		NONE,
	}

    HandlerRegistration addKillRequestEventHandler(SimpleEvent.Handler<String> handler);

    // tool wants to be dragged around with the mouse
    HandlerRegistration addMoveStartEventHandler(SimpleEvent.Handler<MouseEvent<?>> handler);

    // Tool wants to move an offset
    HandlerRegistration addSelfMoveRequestEventHandler(SimpleEvent.Handler<Point2D> handler);



    void setElementData(ElementData data); // non-generic version of setValue

    // Notifies the tool that it became active/inactive in the worksheet.
    void setActive(boolean isActive);

    // Start handling events
    void bind();

    ResizeMode getResizeMode();
    boolean canRotate();

    void setViewMode(boolean isViewMode);

    //TODO: Replace with a custom Resize event.
    void onResize();
}
