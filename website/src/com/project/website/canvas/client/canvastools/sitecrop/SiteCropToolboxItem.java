package com.project.website.canvas.client.canvastools.sitecrop;

import com.project.website.canvas.client.canvastools.base.CanvasTool;
import com.project.website.canvas.client.canvastools.base.CanvasToolFactory;
import com.project.website.canvas.client.canvastools.base.ToolboxItem;
import com.project.website.canvas.client.resources.CanvasResources;
import com.project.website.canvas.shared.data.ElementData;

public class SiteCropToolboxItem implements ToolboxItem {

    private static SiteCropToolFactory factory = new SiteCropToolFactory();

    @Override
    public String getToolboxIconStyle() {
        return CanvasResources.INSTANCE.main().toolboxCropSiteIconStyle();
    }

    @Override
    public String getDragIconStyle() {
        return "";
    }

    @Override
    public String getCanvasStyleInCreateMode() {
        return CanvasResources.INSTANCE.main().cropSiteCreateModeCanvasStyle();
    }

    @Override
    public String getToolboxIconToolTip() {
        return "Crop Site";
    }

    @Override
    public CanvasToolFactory<? extends CanvasTool<? extends ElementData>> getToolFactory() {
        return factory;
    }

    @Override
    public boolean createOnMouseDown()
    {
        // TODO Auto-generated method stub
        return false;
    }
}
