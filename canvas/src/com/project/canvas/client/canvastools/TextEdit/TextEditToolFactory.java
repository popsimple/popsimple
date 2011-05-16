package com.project.canvas.client.canvastools.TextEdit;

import com.google.gwt.user.client.ui.Widget;
import com.project.canvas.client.canvastools.base.CanvasToolFactoryBase;
import com.project.canvas.shared.data.Point2D;

public class TextEditToolFactory extends CanvasToolFactoryBase<TextEditTool>
{
	public TextEditTool create() {
		return new TextEditTool();
	}
	@Override
	public boolean isOneShot() {
		return false;
	}
	@Override
	public Widget getFloatingWidget() {
		return null;
	}
	@Override
	public Point2D getCreationOffset() {
		return new Point2D(-22, -52);
	}
}