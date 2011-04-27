package com.project.canvas.client.canvastools.TaskList;

import com.project.canvas.client.canvastools.CanvasTool;
import com.project.canvas.client.canvastools.CanvasToolFactory;
import com.project.canvas.client.canvastools.ToolboxItem;
import com.project.canvas.client.resources.CanvasResources;

public class TaskListToolboxItem implements ToolboxItem 
{
	@Override
	public String getToolboxIconStyle() {
		return CanvasResources.INSTANCE.main().toolboxTaskListIconStyle();
	}

	@Override
	public String getCanvasStyleInCreateMode() {
		return CanvasResources.INSTANCE.main().taskListCreateModeCanvasStyle();
	}

	@Override
	public String getDragIconStyle() {
		return "";
	}

	@Override
	public String getToolboxIconToolTip() {
		return "Task List";
	}

	@Override
	public CanvasToolFactory<CanvasTool> getToolFactory() {
		// TODO Auto-generated method stub
		return null;
	}
}
