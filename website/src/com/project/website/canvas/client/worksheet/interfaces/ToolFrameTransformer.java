package com.project.website.canvas.client.worksheet.interfaces;

import java.util.Collection;

import com.google.gwt.event.dom.client.MouseEvent;
import com.project.shared.data.Point2D;
import com.project.website.canvas.client.canvastools.base.CanvasToolFrameImpl;

public interface ToolFrameTransformer
{

     ElementDragManager getElementDragManager();

     void setToolFramePosition(final CanvasToolFrameImpl toolFrame, Point2D pos);

     void startDragCanvasToolFrame(final CanvasToolFrameImpl toolFrame, final MouseEvent<?> startEvent);
     void startDragCanvasToolFrames(Collection<CanvasToolFrameImpl> toolFrames, MouseEvent<?> startEvent);

     void startResizeCanvasToolFrame(final CanvasToolFrameImpl toolFrame, final MouseEvent<?> startEvent);

     void startRotateCanvasToolFrame(final CanvasToolFrameImpl toolFrame, MouseEvent<?> startEvent);

}