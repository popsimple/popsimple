package com.project.canvas.client.canvastools;

import com.project.canvas.client.shared.Factory;

public interface CanvasToolFactory<T extends CanvasTool> extends Factory<T> {
	public String getToolboxIconStyle();
	public String getDragIconStyle();
	public String getCanvasStyleInCreateMode();
	public String getToolboxIconToolTip();
}
