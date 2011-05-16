package com.project.canvas.client.canvastools.TaskList;

import com.project.canvas.client.canvastools.base.CanvasToolFactoryBase;

public class TaskListToolFactory extends CanvasToolFactoryBase<TaskListWidget>
{
	public TaskListWidget create() {
		return new TaskListWidget();
	}

}
