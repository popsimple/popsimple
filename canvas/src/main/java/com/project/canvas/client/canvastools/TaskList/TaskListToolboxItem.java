package com.project.canvas.client.canvastools.TaskList;

import com.project.canvas.client.canvastools.CanvasTool;
import com.project.canvas.client.canvastools.CanvasToolFactory;
import com.project.canvas.client.canvastools.ToolboxItem;
import com.project.canvas.client.resources.CanvasResources;

public class TaskListToolboxItem implements ToolboxItem 
{
	public String getToolboxIconStyle() {
		return CanvasResources.INSTANCE.main().toolboxTaskListIconStyle();
	}

	public String getCanvasStyleInCreateMode() {
		return CanvasResources.INSTANCE.main().taskListCreateModeCanvasStyle();
	}

	public String getDragIconStyle() {
		return "";
	}

	public String getToolboxIconToolTip() {
		return "Task List";
	}

	public CanvasToolFactory<CanvasTool> getToolFactory() {
		// TODO Auto-generated method stub
		return null;
	}
}
