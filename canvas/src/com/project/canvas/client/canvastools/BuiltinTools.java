package com.project.canvas.client.canvastools;

import java.util.ArrayList;
import com.project.canvas.client.canvastools.TaskList.TaskListToolboxItem;
import com.project.canvas.client.canvastools.TextEdit.TextEditToolboxItem;

public class BuiltinTools {
	public static Iterable<ToolboxItem> getTools() {
		ArrayList<ToolboxItem> tools = new ArrayList<ToolboxItem>();
		
		tools.add(new CursorToolboxItem());
		tools.add(new TextEditToolboxItem());
		tools.add(new TaskListToolboxItem());
		
		return tools;
	}
}
