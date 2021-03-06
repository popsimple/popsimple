package com.project.website.canvas.client.canvastools.tasklist;

import com.project.website.canvas.client.canvastools.base.interfaces.CanvasTool;
import com.project.website.canvas.client.canvastools.base.interfaces.CanvasToolFactory;
import com.project.website.canvas.client.canvastools.base.interfaces.ToolboxItem;
import com.project.website.canvas.client.resources.CanvasResources;
import com.project.website.canvas.shared.data.ElementData;

public class TaskListToolboxItem implements ToolboxItem {
    private static final TaskListToolFactory factory = new TaskListToolFactory();

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

    public CanvasToolFactory<? extends CanvasTool<? extends ElementData>> getToolFactory() {
        // TODO Auto-generated method stub
        return TaskListToolboxItem.factory;
    }

    @Override
    public boolean createOnMouseDown()
    {
        // TODO Auto-generated method stub
        return false;
    }
}
