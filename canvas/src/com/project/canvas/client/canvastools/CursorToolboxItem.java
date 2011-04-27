package com.project.canvas.client.canvastools;

import com.project.canvas.client.resources.CanvasResources;

public class CursorToolboxItem implements ToolboxItem 
{
	@Override
	public String getToolboxIconStyle() {
		return CanvasResources.INSTANCE.main().toolboxCursorIconStyle();
	}

	@Override
	public String getCanvasStyleInCreateMode() {
		return CanvasResources.INSTANCE.main().cursorCreateModeCanvasStyle();
	}

	@Override
	public String getDragIconStyle() {
		return "";
	}

	@Override
	public String getToolboxIconToolTip() {
		return "Cursor";
	}

	@Override
	public CanvasToolFactory<CanvasTool> getToolFactory() {
		// TODO Auto-generated method stub
		return null;
	}
}
