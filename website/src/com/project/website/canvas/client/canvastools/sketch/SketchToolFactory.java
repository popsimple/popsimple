package com.project.website.canvas.client.canvastools.sketch;

import com.project.shared.data.Point2D;
import com.project.website.canvas.client.canvastools.base.CanvasToolFactoryBase;
import com.project.website.canvas.client.canvastools.base.interfaces.CanvasToolFactory;
import com.project.website.canvas.client.canvastools.sketch.SketchTool.SpiroCurveType;
import com.project.website.canvas.shared.data.SketchData;
import com.project.website.canvas.shared.data.SketchOptions;

public class SketchToolFactory extends CanvasToolFactoryBase<SketchTool>
    implements CanvasToolFactory<SketchTool>
{
    private static final boolean USE_BEZIER_SMOOTHING = true;
    private static final int DEFAULT_PEN_WIDTH = 5;
    private static final int DEFAULT_ERASER_WIDTH = 50;
    private static final int DEFAULT_PEN_SKIP = DEFAULT_PEN_WIDTH;
    public static final String UNIQUE_ID = "SketchToolFactory";

    private static final int DEFAULT_WIDTH = 500;
    private static final int DEFAULT_HEIGHT = 500;
    private static final DrawingTool DEFAULT_DRAWING_TOOL = DrawingTool.PAINT;

    private SketchOptions defaultSketchOptions = new SketchOptions(DEFAULT_PEN_WIDTH,
                                                                   DEFAULT_PEN_SKIP,
                                                                   DEFAULT_ERASER_WIDTH,
                                                                   DEFAULT_DRAWING_TOOL,
                                                                   "black",
                                                                   USE_BEZIER_SMOOTHING,
                                                                   SpiroCurveType.Circle);


    @Override
    public SketchTool create() {
        SketchData data = new SketchData(UNIQUE_ID);
        data.sketchOptions = new SketchOptions(this.defaultSketchOptions);
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
