package com.project.website.canvas.client.canvastools.base;

import java.util.ArrayList;

import com.project.website.canvas.client.ToolFactories;
import com.project.website.canvas.client.canvastools.CursorToolboxItem;
import com.project.website.canvas.client.canvastools.MoveToolboxItem;
import com.project.website.canvas.client.canvastools.base.interfaces.CanvasTool;
import com.project.website.canvas.client.canvastools.base.interfaces.CanvasToolFactory;
import com.project.website.canvas.client.canvastools.base.interfaces.ToolboxItem;
import com.project.website.canvas.client.canvastools.image.ImageToolboxItem;
import com.project.website.canvas.client.canvastools.map.MapToolboxItem;
import com.project.website.canvas.client.canvastools.sketch.SketchToolboxItem;
import com.project.website.canvas.client.canvastools.textedit.TextEditToolboxItem;
import com.project.website.canvas.client.canvastools.video.VideoToolboxItem;
import com.project.website.canvas.shared.data.ElementData;

public class BuiltinTools
{
    public static final CursorToolboxItem cursorTool = new CursorToolboxItem();
    public static final MoveToolboxItem moveTool = new MoveToolboxItem();

    private static final ArrayList<ToolboxItem> tools = new ArrayList<ToolboxItem>();
    static boolean inited = false;

    public static void init()
    {
        if (inited) {
            return;
        }
        inited = true;
        registerBuiltinToolboxItems();
    }

    private static void registerBuiltinToolboxItems()
    {
        BuiltinTools.tools.add(cursorTool);
        BuiltinTools.tools.add(moveTool);

        BuiltinTools.addToolboxItemWithFactory(new TextEditToolboxItem());
        BuiltinTools.addToolboxItemWithFactory(new ImageToolboxItem());
        BuiltinTools.addToolboxItemWithFactory(new VideoToolboxItem());
        BuiltinTools.addToolboxItemWithFactory(new MapToolboxItem());
        BuiltinTools.addToolboxItemWithFactory(new SketchToolboxItem());
        // TODO: Add these as an option in some "labs" or "advanced settings" box
        // BuiltinTools.addToolboxItemWithFactory(new SiteCropToolboxItem());
        // BuiltinTools.addToolboxItemWithFactory(new TaskListToolboxItem());
    }

    public static Iterable<ToolboxItem> getTools()
    {
        init();
        return tools;
    }

    private static void addToolboxItemWithFactory(ToolboxItem toolboxItem)
    {
        BuiltinTools.tools.add(toolboxItem);
        CanvasToolFactory<? extends CanvasTool<? extends ElementData>> factory = toolboxItem.getToolFactory();
        ToolFactories.INSTANCE.put(factory.getFactoryId(), factory);
    }
}
