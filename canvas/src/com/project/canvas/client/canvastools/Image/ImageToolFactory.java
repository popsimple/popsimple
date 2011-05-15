package com.project.canvas.client.canvastools.Image;

import com.project.canvas.client.canvastools.base.CanvasToolFactory;

public class ImageToolFactory implements CanvasToolFactory<ImageTool>
{
	public ImageTool create() {
		return new ImageTool();
	}

	@Override
	public boolean isOneShot() {
		return true;
	}
}