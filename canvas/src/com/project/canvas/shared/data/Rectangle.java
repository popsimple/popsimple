package com.project.canvas.shared.data;

public class Rectangle {
    protected int left = 0;
    protected int top = 0;
    protected int right = 0;
    protected int bottom = 0;

    public Rectangle(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public boolean isOverlapping(Rectangle rect) {
        if (this.right < rect.left) {
            return false;
        }
        if (this.left > rect.right) {
            return false;
        }
        if (this.bottom < rect.top) {
            return false;
        }
        if (this.top > rect.bottom) {
            return false;
        }
        return true;
    }
    
    public boolean contains(Point2D point) {
    	return this.isOverlapping(new Rectangle(point.getX(), point.getY(), point.getX(), point.getY()));
    }
}
