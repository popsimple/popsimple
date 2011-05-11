package com.project.canvas.client.canvastools.TextEdit;

import com.project.canvas.client.canvastools.base.CanvasToolFactory;

public class TextEditToolFactory implements CanvasToolFactory<TextEditTool>
{
	public TextEditTool create() {
		return new TextEditTool();
	}
}