package com.project.website.canvas.client.canvastools.map;

import com.project.gwtmapstraction.client.mxn.MapProvider;
import com.project.website.canvas.client.canvastools.base.CanvasToolFactoryBase;
import com.project.website.canvas.shared.data.MapData;

public class MapToolFactory extends CanvasToolFactoryBase<MapTool>
{
    public static final String UNIQUE_ID = "MapToolFactory";

    @Override
    public MapTool create()
    {
        MapTool tool = new MapTool();
        MapData mapData = new MapData(UNIQUE_ID);
        mapData.provider = MapProvider.GOOGLE_V3.name();
        tool.setValue(mapData);
        return tool;
    }

    @Override
    public String getFactoryId()
    {
        return MapToolFactory.UNIQUE_ID;
    }
}
