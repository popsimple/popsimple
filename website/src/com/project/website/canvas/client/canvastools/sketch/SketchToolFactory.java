package com.project.website.canvas.client.canvastools.sketch;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.project.website.canvas.client.canvastools.base.CanvasToolFactory;
import com.project.website.canvas.client.canvastools.base.CanvasToolFactoryBase;
import com.project.website.canvas.shared.data.VectorGraphicsData;

public class SketchToolFactory extends CanvasToolFactoryBase<SketchTool>
    implements CanvasToolFactory<SketchTool>
{
    private static final int DEFAULT_PEN_WIDTH = 5;
    public static final String UNIQUE_ID = "SketchToolFactory";

    private int penWidth = DEFAULT_PEN_WIDTH;

    @Override
    public SketchTool create() {
        VectorGraphicsData data = new VectorGraphicsData(UNIQUE_ID);
        data.penWidth = this.penWidth;
        SketchTool tool = new SketchTool();
        tool.setValue(data);
        return tool;
    }

    @Override
    public Widget getFloatingWidget() {
        return new FlowPanel();
    }

    @Override
    public String getFactoryId()
    {
        return SketchToolFactory.UNIQUE_ID;
    }

}
