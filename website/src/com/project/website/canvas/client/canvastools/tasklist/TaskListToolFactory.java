package com.project.website.canvas.client.canvastools.tasklist;

import com.project.website.canvas.client.canvastools.base.CanvasToolFactoryBase;
import com.project.website.canvas.shared.data.TaskListData;

public class TaskListToolFactory extends CanvasToolFactoryBase<TaskListTool> {
    
    //TODO: Set a better uniqueId.
    public static final String UNIQUE_ID = "TaskListToolFactory";
    
    public TaskListTool create() 
    {
        TaskListTool taskListTool = new TaskListTool();
        
        taskListTool.setValue(new TaskListData(UNIQUE_ID));
        return taskListTool;
    }

}
