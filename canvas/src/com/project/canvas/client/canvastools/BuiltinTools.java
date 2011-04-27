package com.project.canvas.client.canvastools;

import java.util.ArrayList;

public class BuiltinTools {
	public static Iterable<CanvasToolFactory<? extends CanvasTool>> getTools() {
		ArrayList<CanvasToolFactory<? extends CanvasTool>> tools = new ArrayList<CanvasToolFactory<? extends CanvasTool>>();
		
		tools.add(new TextEditTool.Maker());
		
		return tools;
	}
}
