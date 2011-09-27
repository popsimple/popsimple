package com.project.website.canvas.client.worksheet.interfaces;

import com.google.gwt.event.dom.client.MouseEvent;
import com.project.shared.data.Point2D;
import com.project.website.canvas.client.canvastools.base.CanvasToolFrame;

public interface ToolFrameTransformer
{
     ElementDragManager getElementDragManager();

     void startDragCanvasToolFrame(CanvasToolFrame toolFrame, MouseEvent<?> startEvent);
     void startDragCanvasToolFrames(Iterable<CanvasToolFrame> toolFrames, MouseEvent<?> startEvent);

     void startResizeCanvasToolFrame(CanvasToolFrame toolFrame, MouseEvent<?> startEvent);
     void startRotateCanvasToolFrame(CanvasToolFrame toolFrame, MouseEvent<?> startEvent);

     void setToolFramePosition(CanvasToolFrame toolFrame, Point2D pos);
}