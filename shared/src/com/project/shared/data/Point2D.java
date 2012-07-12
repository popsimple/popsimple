package com.project.shared.data;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.project.shared.interfaces.ICloneable;

public class Point2D implements Serializable, IsSerializable, ICloneable<Point2D> {
    private static final long serialVersionUID = 1L;
    /** x = 0, y = 0 */
    public static final Point2D zero = new Point2D(0, 0);
    /** x = 1, y = 1 */
    public static final Point2D ones = new Point2D(1, 1);

    // These should have been 'final', but then they will not be serialized.
    private int _x;
    private int _y;

    public Point2D() {
        this._x = 0;
        this._y = 0;
    }

    public Point2D(int x, int y) {
        this._x = x;
        this._y = y;
    }

    public Point2D(Point2D pos)
    {
        this(pos.getX(), pos.getY());
    }

    public Point2D getAbs() {
    	return new Point2D(Math.abs(this._x), Math.abs(this._y));
    }

    public int getX() {
        return this._x;
    }

    public int getY() {
        return this._y;
    }

    @Override
    public boolean equals(Object other) {
    	if (null == other) {
    		return false;
    	}
    	if (other.getClass() != this.getClass()) {
    		return false;
    	}
    	Point2D otherPoint = (Point2D) other;
    	return ((this._x == otherPoint._x) && (this._y == otherPoint._y));
    }

    @Override
    public int hashCode() {
    	return (this._x * 51 + this._y * 37) * 13;
    }

    public Point2D minus(Point2D other) {
        return new Point2D(this._x - other._x, this._y - other._y);
    }

    public Point2D mulCoords(double xMul, double yMul)
    {
        return new Point2D((int) Math.round(this._x * xMul), (int) Math.round(this._y * yMul));
    }

    public Point2D mul(double scalar) {
        return new Point2D((int) Math.round(this._x * scalar), (int) Math.round(this._y * scalar));
    }

    public Point2D plus(Point2D other) {
        return new Point2D(this._x + other._x, this._y + other._y);
    }

    public int dotProduct(Point2D other) {
    	return (this._x * other._x + this._y * other._y);
    }
    
    /***
     * Returns x*x + y*y. Equivalent to point.dotProduct(point) or point.getRadius() squared.
     * @return
     */
    public int getPower()
    {
        return this.dotProduct(this);
    }

    /**
     * Returns x + y (equivalent to p.{@link #dotProduct}({@link #ones}))
     */
    public int sumCoords() {
        return this._x + this._y;
    }

    public double getRadians() {
        return Math.atan2(this._y, this._x);
    }


    public double getRadius() {
        return Math.sqrt(this.getPower());
    }

    /**
     * Returns max(x, y)
     */
    public int getMaxCoord() {
        return Math.max(this._x, this._y);
    }

    public Point2D getRotated(double radians)
    {
    	return this.getRotated(radians, 0, 0);
    }

    /**
     * Returns the result of rotating this point by 90 degrees.
     * This method is much faster than using {@link #getRotated(double)} or its variants for these angles.
     * @param times Number of times to rotate by 90 degrees.
     */
    public Point2D getRotatedBy90Deg()
    {
    	return new Point2D(-this._y, this._x);
    }

	/**
	 * Returns the result of rotating the point around the given axis point
	 */
	public Point2D getRotated(double radians, Point2D axisOffset)
	{
		int tx = axisOffset.getX();
		int ty = axisOffset.getY();
    	return getRotated(radians, tx, ty);
	}

	private Point2D getRotated(double radians, int tx, int ty) {
		double cos = Math.cos(radians);
		double sin = Math.sin(radians);
		int sx = this._x - tx;
		int sy = this._y - ty;
		return new Point2D((int) Math.round(sx * cos - sy * sin + tx),
    			           (int) Math.round(sx * sin + sy * cos + ty));
	}

    public static Point2D fromPolar(double radius, double radians)
    {
    	return new Point2D((int)Math.round(radius * Math.cos(radians)), (int) Math.round(radius * Math.sin(radians)));
    }

    public static Point2D max(Point2D first, Point2D other) {
        return new Point2D(Math.max(first._x, other._x), Math.max(first._y, other._y));
    }

    public static Point2D min(Point2D first, Point2D other) {
        return new Point2D(Math.min(first._x, other._x), Math.min(first._y, other._y));
    }

    /**
     * Returns a new point which is this point's coordinates limited to be at least <code>min</code> and at most
     * <code>max</code>, with priority to being more than <code>min</code>
     */
    public Point2D limitTo(Point2D min, Point2D max)
    {
        return Point2D.max(min, Point2D.min(this, max));
    }

    @Override
    public String toString()
    {
        return "Point2D(x=" + this.getX() +  ", y=" + this.getY() + ")";
    }

    /**
     * Returns a vector in the same direction with unit size.
     */
    public Point2D getNormalized()
    {
        return this.getNormalized(1);
    }

    /**
     * Returns a vector in the same direction with the given size.
     */
    public Point2D getNormalized(double size)
    {
        if ((0 == this._x) && (0 == this._y)) {
            return new Point2D(0, 0);
        }
        return this.mul(size/this.getRadius());
    }

    /**
     * Returns a unit normal (unit orthogonal vector) to this one.
     * In other words, a vector that is perpendicular to this and has radius = 1.
     */
    public Point2D getUnitNormal()
    {
        Point2D normalized = this.getNormalized();
        return new Point2D(-normalized.getY(), normalized.getX());
    }

    @Override
    public Point2D getClone()
    {
        return new Point2D(this);
    }
}
