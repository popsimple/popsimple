package com.project.canvas.client.canvastools.base;

import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.shared.data.ElementData;
import com.project.canvas.shared.data.Point2D;

// TODO change getvalue to updateValue to reflect the fact that it mutates the instance of data that was given in the setValue?
public interface CanvasTool<T extends ElementData> extends IsWidget, TakesValue<T> {
	
	public enum ResizeMode {
		BOTH,
		UNIFORM,
		WIDTH_ONLY,
		HEIGHT_ONLY,
		NONE,
	}
    SimpleEvent<String> getKillRequestedEvent();

    // tool wants to be dragged around with the mouse
    HandlerRegistration addMoveStartEventHandler(SimpleEvent.Handler<MouseEvent<?>> handler);
    
    // Tool wants to move an offset
    HandlerRegistration addMoveEventHandler(SimpleEvent.Handler<Point2D> handler);

    void setElementData(ElementData data); // non-generic version of setValue

    // Notifies the tool that it became active/inactive in the worksheet.
    void setActive(boolean isActive);

    // Start handling events
    void bind();
    
    ResizeMode getResizeMode();
    boolean canRotate();
    
    void setViewMode(boolean isViewMode);
}
