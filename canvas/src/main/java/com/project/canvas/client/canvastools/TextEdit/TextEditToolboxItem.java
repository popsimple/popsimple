package com.project.canvas.client.canvastools.TextEdit;

import com.project.canvas.client.canvastools.CanvasTool;
import com.project.canvas.client.canvastools.CanvasToolFactory;
import com.project.canvas.client.canvastools.ToolboxItem;
import com.project.canvas.client.resources.CanvasResources;

public class TextEditToolboxItem implements ToolboxItem 
{
	private static TextEditToolFactory factory = new TextEditToolFactory();
	
	public String getToolboxIconStyle() {
		return CanvasResources.INSTANCE.main().toolboxTextIconStyle();
	}

	public String getCanvasStyleInCreateMode() {
		return CanvasResources.INSTANCE.main().textBoxCreateModeCanvasStyle();
	}

	public String getDragIconStyle() {
		return "";
	}

	public String getToolboxIconToolTip() {
		return "Text tool";
	}

	public CanvasToolFactory<? extends CanvasTool> getToolFactory() {
		// TODO Auto-generated method stub
		return TextEditToolboxItem.factory;
	}
}
