package com.project.website.canvas.client.canvastools.image;

import com.project.website.canvas.client.canvastools.base.CanvasTool;
import com.project.website.canvas.client.canvastools.base.CanvasToolFactory;
import com.project.website.canvas.client.canvastools.base.ToolboxItem;
import com.project.website.canvas.client.resources.CanvasResources;
import com.project.website.canvas.shared.data.ElementData;

public class ImageToolboxItem implements ToolboxItem {
    private static ImageToolFactory factory = new ImageToolFactory();

    public String getToolboxIconStyle() {
        return CanvasResources.INSTANCE.main().toolboxImageIconStyle();
    }

    public String getCanvasStyleInCreateMode() {
        return CanvasResources.INSTANCE.main().imageCreateModeCanvasStyle();
    }

    public String getDragIconStyle() {
        return "";
    }

    public String getToolboxIconToolTip() {
        return "Image";
    }

    public CanvasToolFactory<? extends CanvasTool<? extends ElementData>> getToolFactory() {
        // TODO Auto-generated method stub
        return ImageToolboxItem.factory;
    }
}