package com.project.website.canvas.client.canvastools.tasklist;

import com.project.website.canvas.client.canvastools.base.CanvasToolFactoryBase;
import com.project.website.canvas.shared.data.TaskData;
import com.project.website.canvas.shared.data.TaskListData;

public class TaskListToolFactory extends CanvasToolFactoryBase<TaskListTool>
{
    // TODO: Set a better uniqueId.
    public static final String UNIQUE_ID = "TaskListToolFactory";

    public static final String DEFAULT_TASK_DESCRIPTION = "Type your task here...";
    public static final String DEFAULT_TASK_LIST_TITLE = "My Task List";

    public TaskListTool create()
    {
        TaskListTool taskListTool = new TaskListTool();

        TaskListData data = new TaskListData(UNIQUE_ID);
        data.title = DEFAULT_TASK_LIST_TITLE;

        data.tasks.add(this.createDefaultTask());

        taskListTool.setValue(data);
        return taskListTool;
    }

    private TaskData createDefaultTask()
    {
        TaskData taskData = new TaskData();
        taskData.description = "Type your task here...";
        taskData.imageUrl = ImageProvider.getDefaultImageUrl();
        return taskData;
    }

    @Override
    public String getFactoryId()
    {
        return TaskListToolFactory.UNIQUE_ID;
    }
}
