package com.project.website.canvas.client.worksheet.interfaces;

import com.project.shared.data.Point2D;
import com.project.website.canvas.client.canvastools.base.CanvasToolFrame;

public interface ToolFrameTransformer
{
     ElementDragManager getElementDragManager();

     void startDragCanvasToolFrame(CanvasToolFrame toolFrame);
     void startDragCanvasToolFrames(Iterable<CanvasToolFrame> toolFrames);

     void startResizeCanvasToolFrame(CanvasToolFrame toolFrame);
     void startRotateCanvasToolFrame(CanvasToolFrame toolFrame);

     void setToolFramePosition(CanvasToolFrame toolFrame, Point2D pos);
}