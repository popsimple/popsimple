package com.project.canvas.client.canvastools.Image;

import com.project.canvas.client.canvastools.base.CanvasToolFactoryBase;

public class ImageToolFactory extends CanvasToolFactoryBase<ImageTool> {
	public ImageTool create() {
		return new ImageTool();
	}
}