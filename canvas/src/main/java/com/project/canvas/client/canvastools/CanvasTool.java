package com.project.canvas.client.canvastools;

import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.IsWidget;
import com.project.canvas.client.shared.events.SimpleEvent;


public interface CanvasTool extends IsWidget, Focusable {
	SimpleEvent<String> getKillRequestedEvent();

	void setFocus(boolean isFocused);
}
