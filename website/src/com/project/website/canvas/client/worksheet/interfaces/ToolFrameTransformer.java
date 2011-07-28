package com.project.website.canvas.client.worksheet.interfaces;

import java.util.Collection;

import com.google.gwt.event.dom.client.MouseEvent;
import com.project.shared.data.Point2D;
import com.project.website.canvas.client.canvastools.base.CanvasToolFrame;

public interface ToolFrameTransformer
{

     ElementDragManager getElementDragManager();

     void setToolFramePosition(final CanvasToolFrame toolFrame, Point2D pos);

     void startDragCanvasToolFrame(final CanvasToolFrame toolFrame, final MouseEvent<?> startEvent);
     void startDragCanvasToolFrames(Collection<CanvasToolFrame> toolFrames, MouseEvent<?> startEvent);

     void startResizeCanvasToolFrame(final CanvasToolFrame toolFrame, final MouseEvent<?> startEvent);

     void startRotateCanvasToolFrame(final CanvasToolFrame toolFrame, MouseEvent<?> startEvent);

}