package com.project.canvas.client.canvastools.TaskList;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;
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

	@Override
	public Widget getFloatingWidget() {
		// TODO Auto-generated method stub
		return this.create();
	}
}
