package com.project.website.canvas.client.canvastools.sitecrop;

import com.project.website.canvas.client.canvastools.base.CanvasToolFactoryBase;

public class SiteCropToolFactory extends CanvasToolFactoryBase<SiteCropTool>{

    public static final String UNIQUE_ID = "SiteCropToolFactory";

    @Override
    public SiteCropTool create() {
        SiteCropTool siteCropTool = new SiteCropTool();
        return siteCropTool;
    }

    @Override
    public String getFactoryId()
    {
        return SiteCropToolFactory.UNIQUE_ID;
    }
}
