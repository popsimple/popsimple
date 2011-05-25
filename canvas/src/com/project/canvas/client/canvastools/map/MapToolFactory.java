package com.project.canvas.client.canvastools.map;

import com.project.canvas.client.canvastools.base.CanvasToolFactoryBase;

public class MapToolFactory extends CanvasToolFactoryBase<MapTool>
{

    @Override
    public MapTool create()
    {
        return new MapTool();
    }

}
