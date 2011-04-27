package com.project.canvas.client.canvastools.TextEdit;

import com.project.canvas.client.canvastools.CanvasToolFactory;

public class TextEditToolFactory implements CanvasToolFactory<TextEditTool>
{
	@Override
	public TextEditTool create() {
		return new TextEditTool();
	}
}