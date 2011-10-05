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

    public static void drawOnto(Canvas source, Canvas dest)
    {
        dest.getContext2d().drawImage(source.getCanvasElement(), 0, 0);
    }

    public static void clear(Canvas canvas)
    {
        canvas.getContext2d().clearRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
    }

}
