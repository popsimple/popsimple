package com.project.canvas.client.canvastools.base;

import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.shared.data.ElementData;


public interface CanvasTool<T extends ElementData> extends IsWidget, TakesValue<T> {
	SimpleEvent<String> getKillRequestedEvent();

	void setElementData(ElementData data); // non-generic version

	void setFocus(boolean isFocused);
}
