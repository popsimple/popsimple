package com.project.canvas.client.canvastools.base;

import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.shared.data.ElementData;


// TODO change getvalue to updateValue to reflect the fact that it mutates the instance of data that was given in the setValue?
public interface CanvasTool<T extends ElementData> extends IsWidget, TakesValue<T> {
	SimpleEvent<String> getKillRequestedEvent();

	void setElementData(ElementData data); // non-generic version

	void setFocus(boolean isFocused);
}
