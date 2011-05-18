package com.project.canvas.client.canvastools.TextEdit;

import com.google.gwt.user.client.ui.Widget;
import com.project.canvas.client.canvastools.base.CanvasToolFactoryBase;

public class TextEditToolFactory extends CanvasToolFactoryBase<TextEditTool> {
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