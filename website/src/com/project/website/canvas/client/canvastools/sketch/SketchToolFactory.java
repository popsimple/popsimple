package com.project.website.canvas.client.canvastools.sketch;

import com.project.website.canvas.client.canvastools.base.CanvasToolFactory;
import com.project.website.canvas.client.canvastools.base.CanvasToolFactoryBase;
import com.project.website.canvas.shared.data.VectorGraphicsData;

public class SketchToolFactory extends CanvasToolFactoryBase<SketchTool> 
    implements CanvasToolFactory<SketchTool> 
{
    public static final String UNIQUE_ID = "SketchToolFactory";

    @Override
    public SketchTool create() {
        VectorGraphicsData data = new VectorGraphicsData(UNIQUE_ID);
        SketchTool tool = new SketchTool();
        tool.setValue(data);
        return tool;
    }

}
