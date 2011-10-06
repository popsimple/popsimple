package com.project.shared.utils;

import java.util.LinkedList;

import com.project.shared.data.Pair;
import com.project.shared.data.Point2D;

public class PointUtils
{
    public enum ConstraintMode {
        NONE, // identity function
        MEAN, // transforms both x and y to (x+y)/2
        KEEP_RATIO, // uses the initialPos to change (x,y) so that the original aspect ratio is preserved
        SNAP_X, // sets y = 0
        SNAP_Y,  // sets x = 0
    }

    public static Point2D constrain(Point2D pos, Point2D initialPos, ConstraintMode mode)
    {
        switch (mode) {
            case NONE:   return pos;
            case KEEP_RATIO:   return constrainKeepRatio(pos, initialPos);
            case MEAN:   return constrainToMean(pos);
            case SNAP_X: return new Point2D(pos.getX(), 0);
            case SNAP_Y: return new Point2D(0         , pos.getY());
            default:
                throw new RuntimeException("Unsupported transformation mode");
        }
    }

    private static Point2D constrainKeepRatio(Point2D pos, Point2D initialPos)
    {
        int y = initialPos.getY();
        int x = initialPos.getX();
        if (y == 0) {
            return new Point2D(pos.getX(), 0);
        }
        if (x == 0) {
            return new Point2D(0, pos.getY());
        }
        double initialRatio = x / (double)y;
        if (pos.getX() > pos.getY()) {
            return new Point2D(pos.getX(), (int)Math.round(pos.getX() / initialRatio));
        }
        return new Point2D((int)Math.round(pos.getY() * initialRatio), pos.getY());
    }

    private static Point2D constrainToMean(Point2D pos)
    {
        int mean = (pos.getX() + pos.getY()) / 2;
        return new Point2D(mean, mean);
    }

    public static Point2D nullToZero(Point2D pos)
    {
        if (null == pos) {
            return new Point2D(Point2D.zero);
        }
        return pos;
    }

    public static class MovingAverage
    {
        LinkedList<Point2D> points = new LinkedList<Point2D>();
        private int _size;

        public MovingAverage(int size) {
            this._size = size;
        }

        public void clear() {
            this.points.clear();
        }

        public void add(Point2D point) {
            this.points.addLast(point);
            if (this.points.size() > this._size) {
                this.points.removeFirst();
            }
        }

        public Point2D getAverage() {
            Point2D result = Point2D.zero;
            for (Point2D point : points) {
                result = result.plus(point);
            }
            return result.mul(1.0 / this.points.size());
        }
    }

    /**
     * Calculates the required control points for a segment of a bezier curve to follow exactly through the given points.
     * @See <a href="http://scaledinnovation.com/analytics/splines/splines.html">Spline Interpolation</a> on scaledinnovation.com
     */
    public static Pair<Point2D, Point2D> getBezierControlPoints(Point2D segmentStart, Point2D segmentEnd, Point2D nextSegmentEnd, double tension)
    {
        double startToEnd = segmentStart.minus(segmentEnd).getRadius();
        double endToNext = segmentEnd.minus(nextSegmentEnd).getRadius();

        double fa = tension * startToEnd / (startToEnd + endToNext);
        double fb = fa - tension;

        final Point2D startToNext = segmentStart.minus(nextSegmentEnd);
        Point2D control1 = startToNext.mul(fa).plus(segmentEnd);
        Point2D control2 = startToNext.mul(fb).plus(segmentEnd);

        return new Pair<Point2D, Point2D>(control1, control2);
    }
}
