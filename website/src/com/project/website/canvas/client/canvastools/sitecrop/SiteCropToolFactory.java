package com.project.website.canvas.client.canvastools.sitecrop;

import com.project.website.canvas.client.canvastools.base.CanvasToolFactoryBase;

public class SiteCropToolFactory extends CanvasToolFactoryBase<SiteCropTool>{

    @Override
    public SiteCropTool create() {
        SiteCropTool siteCropTool = new SiteCropTool();
        return siteCropTool;
    }
}
