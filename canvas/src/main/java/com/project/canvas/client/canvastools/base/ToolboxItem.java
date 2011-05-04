package com.project.canvas.client.canvastools.base;

public interface ToolboxItem {
	public String getToolboxIconStyle();
	public String getDragIconStyle();
	public String getCanvasStyleInCreateMode();
	public String getToolboxIconToolTip();
	
	public CanvasToolFactory<? extends CanvasTool> getToolFactory();
}
