package com.project.canvas.shared.data;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Point2D implements Serializable, IsSerializable {
    private static final long serialVersionUID = 1L;

    public static final Point2D zero = new Point2D(0, 0);

    private int x;
    private int y;

    public Point2D() {
    }

    public Point2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Point2D minus(Point2D other) {
        return new Point2D(this.x - other.x, this.y - other.y);
    }

    public Point2D plus(Point2D other) {
        return new Point2D(this.x + other.x, this.y + other.y);
    }

    public Point2D mul(double scalar) {
        return new Point2D((int) (this.x * scalar), (int) (this.y * scalar));
    }

    public Point2D mul(int scalar) {
        return new Point2D(this.x * scalar, this.y * scalar);
    }

    public double radians() {
        return Math.atan2(y, x);
    }

    public double radius() {
        return Math.sqrt(x * x + y * y);
    }
    
    public Point2D abs() {
    	return new Point2D(Math.abs(this.x), Math.abs(this.y));
    }

    public static Point2D min(Point2D first, Point2D other) {
        return new Point2D(Math.min(first.x, other.x), Math.min(first.y, other.y));
    }

    public static Point2D max(Point2D first, Point2D other) {
        return new Point2D(Math.max(first.x, other.x), Math.max(first.y, other.y));
    }
    
    
    public static Point2D fromPolar(double radius, double radians)
    {
    	return new Point2D((int)(radius * Math.cos(radians)), (int) (radius * Math.sin(radians)));
    }
    
    public Point2D rotate(double radians)
    {
    	double newAngle = this.radians() + radians;
    	return Point2D.fromPolar(this.radius(), newAngle);
    }

}
