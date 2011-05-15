package com.project.canvas.client.canvastools.Image;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
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

	@Override
	public Widget getFloatingWidget() {
		// TODO Auto-generated method stub
		return this.create();
	}
}