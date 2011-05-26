package com.project.canvas.client.canvastools.map;

import com.project.canvas.client.canvastools.base.CanvasToolFactoryBase;
import com.project.canvas.shared.data.MapData;

public class MapToolFactory extends CanvasToolFactoryBase<MapTool>
{
    public static final String UNIQUE_ID = "MapToolFactory";

    @Override
    public MapTool create()
    {
        MapTool tool = new MapTool();
        tool.setValue(new MapData(UNIQUE_ID));
        return tool;
    }

}
