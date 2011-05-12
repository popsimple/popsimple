package com.project.canvas.client.canvastools;

import com.project.canvas.client.canvastools.base.CanvasTool;
import com.project.canvas.client.canvastools.base.CanvasToolFactory;
import com.project.canvas.client.canvastools.base.ToolboxItem;
import com.project.canvas.client.resources.CanvasResources;
import com.project.canvas.shared.data.ElementData;

public class CursorToolboxItem implements ToolboxItem 
{
	public String getToolboxIconStyle() {
		return CanvasResources.INSTANCE.main().toolboxCursorIconStyle();
	}

	public String getCanvasStyleInCreateMode() {
		return CanvasResources.INSTANCE.main().cursorCreateModeCanvasStyle();
	}

	public String getDragIconStyle() {
		return "";
	}

	public String getToolboxIconToolTip() {
		return "Cursor";
	}

	public CanvasToolFactory<CanvasTool<ElementData>> getToolFactory() {
		// TODO Auto-generated method stub
		return null;
	}
}
