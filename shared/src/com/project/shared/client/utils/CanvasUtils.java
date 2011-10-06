package com.project.shared.client.utils;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d.Composite;
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

    public static void drawOnto(Canvas source, Canvas dest, Composite composite)
    {
        dest.getContext2d().save();
        dest.getContext2d().setGlobalCompositeOperation(composite);
        dest.getContext2d().drawImage(source.getCanvasElement(), 0, 0);
        dest.getContext2d().restore();
    }

    public static void clear(Canvas canvas)
    {
        canvas.getContext2d().clearRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
    }

}
