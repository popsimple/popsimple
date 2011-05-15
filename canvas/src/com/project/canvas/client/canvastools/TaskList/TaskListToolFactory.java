package com.project.canvas.client.canvastools.TaskList;

import com.project.canvas.client.canvastools.base.CanvasToolFactory;

public class TaskListToolFactory implements CanvasToolFactory<TaskListWidget>
{
	public TaskListWidget create() {
		return new TaskListWidget();
	}

	@Override
	public boolean isOneShot() {
		return true;
	}
}
