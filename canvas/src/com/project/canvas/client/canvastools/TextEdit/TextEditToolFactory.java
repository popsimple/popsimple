package com.project.canvas.client.canvastools.TextEdit;

import com.google.gwt.user.client.ui.Widget;
import com.project.canvas.client.canvastools.base.CanvasToolFactory;

public class TextEditToolFactory implements CanvasToolFactory<TextEditTool>
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
}