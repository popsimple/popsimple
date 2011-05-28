package com.project.canvas.client.canvastools.map;

import com.project.canvas.client.canvastools.base.CanvasTool;
import com.project.canvas.client.canvastools.base.CanvasToolFactory;
import com.project.canvas.client.canvastools.base.ToolboxItem;
import com.project.canvas.client.resources.CanvasResources;
import com.project.canvas.shared.data.ElementData;

public class MapToolboxItem implements ToolboxItem
{

    private final MapToolFactory mapToolFactory = new MapToolFactory();

    @Override
    public String getToolboxIconStyle()
    {
        return CanvasResources.INSTANCE.main().toolboxMapIconStyle();
    }

    @Override
    public String getDragIconStyle()
    {
        return "";
    }

    @Override
    public String getCanvasStyleInCreateMode()
    {
        return CanvasResources.INSTANCE.main().mapCreateModeCanvasStyle();
    }

    @Override
    public String getToolboxIconToolTip()
    {
        return "Map";
    }

    @Override
    public CanvasToolFactory<? extends CanvasTool<? extends ElementData>> getToolFactory()
    {
        return mapToolFactory;
    }

}
