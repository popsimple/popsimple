package com.project.canvas.client.canvastools.base;

import com.google.gwt.user.client.ui.Widget;
import com.project.canvas.client.shared.Factory;

public interface CanvasToolFactory<T extends CanvasTool<?>> extends Factory<T> {
	boolean isOneShot();
	Widget getFloatingWidget(); 
}
