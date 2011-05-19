package com.project.canvas.client.canvastools.base;

import java.util.ArrayList;

import com.project.canvas.client.canvastools.CursorToolboxItem;
import com.project.canvas.client.canvastools.Image.ImageToolboxItem;
import com.project.canvas.client.canvastools.Image.BingImage.BingImageToolboxItem;
import com.project.canvas.client.canvastools.TaskList.TaskListToolboxItem;
import com.project.canvas.client.canvastools.TextEdit.TextEditToolboxItem;

public class BuiltinTools {
    public static final CursorToolboxItem cursorTool = new CursorToolboxItem();

    protected static final ArrayList<ToolboxItem> tools = new ArrayList<ToolboxItem>();
    static boolean inited = false;

    static void init() {
        if (inited) {
            return;
        }
        tools.add(cursorTool);
        tools.add(new TextEditToolboxItem());
        tools.add(new TaskListToolboxItem());
        tools.add(new ImageToolboxItem());
        //TODO: Temporarily until the unified search provider will be done.
        tools.add(new BingImageToolboxItem());
    }

    public static Iterable<ToolboxItem> getTools() {
        init();
        return tools;
    }

}
