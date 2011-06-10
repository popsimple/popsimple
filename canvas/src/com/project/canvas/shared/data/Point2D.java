package com.project.canvas.shared.data;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Point2D implements Serializable, IsSerializable {
    private static final long serialVersionUID = 1L;

    public static final Point2D zero = new Point2D(0, 0);

    private int _x;
    private int _y;

    public Point2D() {}

    public Point2D(int x, int y) {
        this._x = x;
        this._y = y;
    }

    public Point2D abs() {
    	return new Point2D(Math.abs(this._x), Math.abs(this._y));
    }

    public int getX() {
        return this._x;
    }

    public int getY() {
        return this._y;
    }
    
    public void setX(int x) {
        this._x = x;
    }

    public void setY(int y) {
        this._y = y;
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
    	return (this._x + this._y + 37) * 13;
    }
    
    public Point2D minus(Point2D other) {
        return new Point2D(this._x - other._x, this._y - other._y);
    }

    public Point2D mul(double scalar) {
        return new Point2D((int) (this._x * scalar), (int) (this._y * scalar));
    }
    public Point2D mul(int scalar) {
        return new Point2D(this._x * scalar, this._y * scalar);
    }
    
    public Point2D plus(Point2D other) {
        return new Point2D(this._x + other._x, this._y + other._y);
    }

    public double radians() {
        return Math.atan2(this._y, this._x);
    }
    
    
    public double radius() {
        return Math.sqrt(this._x * this._x + this._y * this._y);
    }
    
    public Point2D rotate(double radians)
    {
    	double newAngle = this.radians() + radians;
    	return Point2D.fromPolar(this.radius(), newAngle);
    }

	/**
	 * Transforms a given point to and from rotated and unrotated coordinates, relative to the given axis point 
	 * @param radians rotation angle in radians
	 * @param axisOffset 
	 * @param toRotated true = from unrotated to rotated, false = opposite transformation
	 * @return transformed point
	 */
	public Point2D rotate(double radians, Point2D axisOffset, boolean toRotated) 
	{
		int direction = toRotated ? 1 : -1;
		return this.minus(axisOffset.rotate(radians).minus(axisOffset).mul(direction));
	}

    public static Point2D fromPolar(double radius, double radians)
    {
    	return new Point2D((int)(radius * Math.cos(radians)), (int) (radius * Math.sin(radians)));
    }
    
    public static Point2D max(Point2D first, Point2D other) {
        return new Point2D(Math.max(first._x, other._x), Math.max(first._y, other._y));
    }

    public static Point2D min(Point2D first, Point2D other) {
        return new Point2D(Math.min(first._x, other._x), Math.min(first._y, other._y));
    }

}
