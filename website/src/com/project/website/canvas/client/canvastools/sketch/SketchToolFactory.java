package com.project.website.canvas.client.canvastools.sketch;

import com.project.shared.data.Point2D;
import com.project.website.canvas.client.canvastools.base.CanvasToolFactory;
import com.project.website.canvas.client.canvastools.base.CanvasToolFactoryBase;
import com.project.website.canvas.shared.data.VectorGraphicsData;

public class SketchToolFactory extends CanvasToolFactoryBase<SketchTool>
    implements CanvasToolFactory<SketchTool>
{
    private static final int DEFAULT_PEN_WIDTH = 5;
    public static final String UNIQUE_ID = "SketchToolFactory";

    private final static int DEFAULT_WIDTH = 400;
    private final static int DEFAULT_HEIGHT = 400;


    private int penWidth = DEFAULT_PEN_WIDTH;

    @Override
    public SketchTool create() {
        VectorGraphicsData data = new VectorGraphicsData(UNIQUE_ID);
        data.penWidth = this.penWidth;
        SketchTool tool = new SketchTool(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        tool.setValue(data);
        return tool;
    }

    @Override
    public String getFactoryId()
    {
        return SketchToolFactory.UNIQUE_ID;
    }


    @Override
    public Point2D getCreationOffset() {
        return new Point2D(-DEFAULT_WIDTH/2, -DEFAULT_HEIGHT/2);
    }
}
