package com.project.website.canvas.client.canvastools.sketch;

import com.project.website.canvas.client.canvastools.base.CanvasTool;
import com.project.website.canvas.client.canvastools.base.CanvasToolFactory;
import com.project.website.canvas.client.canvastools.base.ToolboxItem;
import com.project.website.canvas.client.resources.CanvasResources;
import com.project.website.canvas.shared.data.ElementData;

public class SketchToolboxItem implements ToolboxItem {

    private static final SketchToolFactory _factory = new SketchToolFactory();

    @Override
    public String getToolboxIconStyle() {
        return CanvasResources.INSTANCE.main().toolboxSketchIconStyle();
    }

    @Override
    public String getDragIconStyle() {
        return "";
    }

    @Override
    public String getCanvasStyleInCreateMode() {
        return CanvasResources.INSTANCE.main().sketchCreateModeCanvasStyle();
    }

    @Override
    public String getToolboxIconToolTip() {
        return "Sketch";
    }

    @Override
    public CanvasToolFactory<? extends CanvasTool<? extends ElementData>> getToolFactory() {
        return SketchToolboxItem._factory;
    }
}
