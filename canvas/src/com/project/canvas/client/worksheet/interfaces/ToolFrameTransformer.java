package com.project.canvas.client.worksheet.interfaces;

import com.google.gwt.event.dom.client.MouseEvent;
import com.project.canvas.client.canvastools.base.CanvasToolFrame;
import com.project.canvas.shared.data.Point2D;

public interface ToolFrameTransformer
{

    public abstract ElementDragManager getElementDragManager();

    public abstract void setToolFramePosition(final CanvasToolFrame toolFrame, Point2D pos);

    public abstract void startDragCanvasToolFrame(final CanvasToolFrame toolFrame, final MouseEvent<?> startEvent);

    public abstract void startResizeCanvasToolFrame(final CanvasToolFrame toolFrame, final MouseEvent<?> startEvent);

    public abstract void startRotateCanvasToolFrame(final CanvasToolFrame toolFrame, MouseEvent<?> startEvent);

}