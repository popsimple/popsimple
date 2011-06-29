package com.project.website.canvas.client.canvastools.video;

import com.project.website.canvas.client.canvastools.base.CanvasTool;
import com.project.website.canvas.client.canvastools.base.CanvasToolFactory;
import com.project.website.canvas.client.canvastools.base.ToolboxItem;
import com.project.website.canvas.client.resources.CanvasResources;
import com.project.website.canvas.shared.data.ElementData;

public class VideoToolboxItem implements ToolboxItem
{
    private static VideoToolFactory factory = new VideoToolFactory();
    
    @Override
    public String getToolboxIconStyle() {
        return CanvasResources.INSTANCE.main().toolboxVideoIconStyle();
    }

    @Override
    public String getDragIconStyle() {
        return "";
    }

    @Override
    public String getCanvasStyleInCreateMode() {
        return CanvasResources.INSTANCE.main().videoCreateModeCanvasStyle();
    }

    @Override
    public String getToolboxIconToolTip() {
        return "Video";
    }

    @Override
    public CanvasToolFactory<? extends CanvasTool<? extends ElementData>> getToolFactory() {
        // TODO Auto-generated method stub
        return factory;
    }

}
