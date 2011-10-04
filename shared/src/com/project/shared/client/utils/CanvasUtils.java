package com.project.shared.client.utils;

import com.google.gwt.canvas.client.Canvas;
import com.project.shared.data.Point2D;

public class CanvasUtils
{
    public static void setCoordinateSpaceSize(Canvas canvas, Point2D size)
    {
        canvas.setCoordinateSpaceWidth(size.getX());
        canvas.setCoordinateSpaceHeight(size.getY());
    }

    public static Point2D getCoorinateSpaceSize(Canvas canvas)
    {
        return new Point2D(canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
    }
}
