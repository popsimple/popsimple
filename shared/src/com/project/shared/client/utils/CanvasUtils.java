package com.project.shared.client.utils;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.Composite;
import com.google.gwt.dom.client.Element;
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
        CanvasUtils.drawOnto(source, dest, composite, true, false);
    }

    public static void drawOnto(Canvas source, Canvas dest, Composite composite, boolean disableShadow, boolean clearDestRectBeforeCopy)
    {
        final Context2d destContext = dest.getContext2d();
        destContext.save();
        if (disableShadow) {
            destContext.setShadowBlur(0);
            destContext.setShadowColor("transparent");
        }
        if (clearDestRectBeforeCopy) {
            destContext.clearRect(0, 0, source.getCoordinateSpaceWidth(), source.getCoordinateSpaceHeight());
        }
        String compositeOperation = destContext.getGlobalCompositeOperation();
        destContext.setGlobalCompositeOperation(composite);
//        destContext.setTransform(1, 0, 0, 1, 0, 0);
//        destContext.beginPath();
//        destContext.rect(0, 0, source.getCoordinateSpaceWidth(), source.getCoordinateSpaceHeight());
//        destContext.closePath();
//        destContext.clip();
//        // we should have just done drawImage with destination width/height set:
//        // but both firefox & IE seem to be doing it wrong and clear out the whole of dest if using COPY (or is the problem elsewhere? did not investigate)
        destContext.drawImage(source.getCanvasElement(), 0, 0, source.getCoordinateSpaceWidth(), source.getCoordinateSpaceHeight());
        destContext.setGlobalCompositeOperation(compositeOperation);
        destContext.restore();
    }

    public static void clear(Canvas canvas)
    {
        canvas.getContext2d().clearRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
    }


    // TODO: for IE <= 8, call this instead of Canvas.getContext2d
    private static final native Context2d getExplorerContext2d(Element canvasElement) /*-{
        $wnd.G_vmlCanvasManager.initElement(el);
        return el.getContext('2d');
    }-*/;
}
