package com.project.canvas.client.canvastools.base;

import java.util.ArrayList;

import com.project.canvas.client.canvastools.CursorToolboxItem;
import com.project.canvas.client.canvastools.image.ImageToolboxItem;
import com.project.canvas.client.canvastools.tasklist.TaskListToolboxItem;
import com.project.canvas.client.canvastools.textedit.TextEditToolboxItem;
import com.project.canvas.client.canvastools.video.VideoToolboxItem;

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
        tools.add(new VideoToolboxItem());
    }

    public static Iterable<ToolboxItem> getTools() {
        init();
        return tools;
    }

}
