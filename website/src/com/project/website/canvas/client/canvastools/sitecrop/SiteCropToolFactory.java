package com.project.website.canvas.client.canvastools.sitecrop;

import com.project.website.canvas.client.canvastools.base.CanvasToolFactoryBase;
import com.project.website.canvas.shared.data.SiteCropElementData;

public class SiteCropToolFactory extends CanvasToolFactoryBase<SiteCropTool>{

    public static final String UNIQUE_ID = "SiteCropToolFactory";

    @Override
    public SiteCropTool create() {
        SiteCropTool siteCropTool = new SiteCropTool();
        SiteCropElementData data = new SiteCropElementData(UNIQUE_ID);
        siteCropTool.setValue(new SiteCropElementData(UNIQUE_ID));
        return siteCropTool;
    }

    @Override
    public String getFactoryId()
    {
        return SiteCropToolFactory.UNIQUE_ID;
    }
}
