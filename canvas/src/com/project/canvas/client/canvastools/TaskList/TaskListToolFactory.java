package com.project.canvas.client.canvastools.tasklist;

import com.project.canvas.client.canvastools.base.CanvasToolFactoryBase;

public class TaskListToolFactory extends CanvasToolFactoryBase<TaskListTool> {
    public TaskListTool create() {
        return new TaskListTool();
    }

}
