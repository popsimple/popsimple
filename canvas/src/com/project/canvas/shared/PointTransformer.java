package com.project.canvas.shared;

import com.project.canvas.shared.data.Point2D;

public class PointTransformer
{
    public enum TransformationMode {
        NONE, // identity function
        MEAN, // transforms both x and y to (x+y)/2
        SNAP_X, // sets y = 0
        SNAP_Y, // sets x = 0
    }
    
    public static Point2D Transform(Point2D pos, TransformationMode mode) 
    {
        switch (mode) {
            case NONE:   return pos;
            case MEAN:   return transformMean(pos);
            case SNAP_X: return new Point2D(pos.getX(), 0);
            case SNAP_Y: return new Point2D(0         , pos.getY());
            default:
                throw new RuntimeException("Unsupported transformation mode");
        }
    }

    private static Point2D transformMean(Point2D pos)
    {
        int mean = (pos.getX() + pos.getY()) / 2;
        return new Point2D(mean, mean);
    }
}
