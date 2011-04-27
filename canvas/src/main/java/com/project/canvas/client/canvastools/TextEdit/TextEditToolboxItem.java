package com.project.canvas.client.canvastools.TextEdit;

import com.project.canvas.client.canvastools.CanvasTool;
import com.project.canvas.client.canvastools.CanvasToolFactory;
import com.project.canvas.client.canvastools.ToolboxItem;
import com.project.canvas.client.resources.CanvasResources;

public class TextEditToolboxItem implements ToolboxItem 
{
	private static TextEditToolFactory factory = new TextEditToolFactory();
	
	@Override
	public String getToolboxIconStyle() {
		return CanvasResources.INSTANCE.main().toolboxTextIconStyle();
	}

	@Override
	public String getCanvasStyleInCreateMode() {
		return CanvasResources.INSTANCE.main().textBoxCreateModeCanvasStyle();
	}

	@Override
	public String getDragIconStyle() {
		return "";
	}

	@Override
	public String getToolboxIconToolTip() {
		return "Text tool";
	}

	@Override
	public CanvasToolFactory<? extends CanvasTool> getToolFactory() {
		// TODO Auto-generated method stub
		return TextEditToolboxItem.factory;
	}
}
