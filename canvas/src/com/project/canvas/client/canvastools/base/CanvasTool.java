package com.project.canvas.client.canvastools.base;

import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.IsWidget;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.shared.data.ElementData;


public interface CanvasTool<T extends ElementData> extends IsWidget, Focusable {
	SimpleEvent<String> getKillRequestedEvent();

	T getData();
	void setData(T data);
	void setElementData(ElementData data); // non-generic version

	void setFocus(boolean isFocused);
}
