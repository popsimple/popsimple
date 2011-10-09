package com.project.website.canvas.client.worksheet.interfaces;

import com.project.shared.data.Point2D;
import com.project.website.canvas.client.canvastools.base.interfaces.CanvasToolFrame;

public interface ToolFrameTransformer
{
     ElementDragManager getElementDragManager();

     void startDragCanvasToolFrame(CanvasToolFrame toolFrame);
     void startDragCanvasToolFrames(Iterable<CanvasToolFrame> toolFrames);

     void startResizeCanvasToolFrame(CanvasToolFrame toolFrame);
     void startRotateCanvasToolFrame(CanvasToolFrame toolFrame);

     void setToolFramePosition(CanvasToolFrame toolFrame, Point2D pos);

    double getGridResolution();
    void setGridResolution(double gridResolution);

    boolean isSnapToGrid();
    void setSnapToGrid(boolean snapToGrid);

    Point2D applySnapToGrid(Point2D sizeDelta);
}