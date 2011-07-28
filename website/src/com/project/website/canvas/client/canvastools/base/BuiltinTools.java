package com.project.website.canvas.client.canvastools.base;

import java.util.ArrayList;

import com.project.website.canvas.client.ToolFactories;
import com.project.website.canvas.client.canvastools.CursorToolboxItem;
import com.project.website.canvas.client.canvastools.image.ImageToolFactory;
import com.project.website.canvas.client.canvastools.image.ImageToolboxItem;
import com.project.website.canvas.client.canvastools.map.MapToolFactory;
import com.project.website.canvas.client.canvastools.map.MapToolboxItem;
import com.project.website.canvas.client.canvastools.tasklist.TaskListToolFactory;
import com.project.website.canvas.client.canvastools.tasklist.TaskListToolboxItem;
import com.project.website.canvas.client.canvastools.textedit.TextEditToolFactory;
import com.project.website.canvas.client.canvastools.textedit.TextEditToolboxItem;
import com.project.website.canvas.client.canvastools.video.VideoToolFactory;
import com.project.website.canvas.client.canvastools.video.VideoToolboxItem;

public class BuiltinTools {
    public static final CursorToolboxItem cursorTool = new CursorToolboxItem();

    protected static final ArrayList<ToolboxItem> tools = new ArrayList<ToolboxItem>();
    static boolean inited = false;

    public static void init() {
        if (inited) {
            return;
        }
        inited = true;
        registerBuiltinToolboxItems();
        registerBuiltinFactories();
    }

    private static void registerBuiltinToolboxItems() {
        tools.add(cursorTool);
        tools.add(new TextEditToolboxItem());
        tools.add(new ImageToolboxItem());
        tools.add(new VideoToolboxItem());
        tools.add(new MapToolboxItem());
        tools.add(new TaskListToolboxItem());
    }

    public static Iterable<ToolboxItem> getTools() {
        init();
        return tools;
    }

    public static void registerBuiltinFactories()
    {
        ToolFactories allFactories = ToolFactories.INSTANCE;
        allFactories.put(ImageToolFactory.UNIQUE_ID, new ImageToolFactory());
        allFactories.put(VideoToolFactory.UNIQUE_ID, new VideoToolFactory());
        allFactories.put(TextEditToolFactory.UNIQUE_ID, new TextEditToolFactory());
        allFactories.put(MapToolFactory.UNIQUE_ID, new MapToolFactory());
        allFactories.put(TaskListToolFactory.UNIQUE_ID, new TaskListToolFactory());
    }
}