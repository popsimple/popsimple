package com.project.shared.data;


public class Rectangle {
    private int left = 0;
    private int top = 0;
    private int right = 0;
    private int bottom = 0;
    /* Rotation axis is center */
    private int rotation = 0;

    public Rectangle() {
    	this(0, 0, 0, 0);
    }
    
    public Rectangle(int left, int top, int right, int bottom) {
        this(left, top, right, bottom, 0);
    }
    
    public Rectangle(int left, int top, int size) {
        this(left, top, left + size, top + size);
    }
    
    public Rectangle(int left, int top, int right, int bottom, int rotation) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.rotation = rotation;
    }

    public boolean contains(Point2D point) {
    	Point2D rotatedPoint = point.rotate(-Math.toRadians(rotation), getCenter(), true);
    	int px = rotatedPoint.getX();
    	int py = rotatedPoint.getY();
    	return ((px >= left) && (px <= right) && (py >= bottom) && (py <= top));
    }

    public Point2D getCenter() {
        return new Point2D((left + right) / 2, (top + bottom) / 2);
    }
    
    /**
     * @return the actual positions of the corners taking rotation of the rectangle into account.
     */
    public Point2D[] getCorners() {
        Point2D[] corners = new Point2D[] {
            new Point2D(right, top), 
            new Point2D(right, bottom),
            new Point2D(left, bottom),
            new Point2D(left, top)
        };
        for (int i = 0; i < corners.length; i++) {
            corners[i] = corners[i].rotate(Math.toRadians(rotation), getCenter(), true);
        }
        return corners;
    }
    
    public int getLeft()
    {
    	return this.left;
    }
    
    public int getTop()
    {
    	return this.top;
    }
    
    public int getRight()
    {
    	return this.right;
    }
    
    public int getBottom()
    {
    	return this.bottom;
    }
    
    public void setLeft(int left)
    {
    	this.left = left;
    }
    
    public void setTop(int top)
    {
    	this.top = top;
    }
    
    public void setRight(int right)
    {
    	this.right = right;
    }
    
    public void setBottom(int bottom)
    {
    	this.bottom = bottom;
    }
    
    public Point2D getSize()
    {
    	return new Point2D(
    			Math.abs((this.right - this.left)),
    			Math.abs((this.bottom - this.top)));
    }
    
    /**
     * @return radius of smallest circle containing the rectangle
     */
    public double externalRadius()
    {
        Point2D corner = new Point2D(right, top);
        return corner.minus(getCenter()).radius();
    }
    
    /**
     * @param other rectangle to compare with
     * @return Whether or not the smallest circles containing each of the two rectangles are overlapping 
     */
    public boolean isExternalCircleOverlapping(Rectangle other) 
    {
        return this.getCenter().minus(other.getCenter()).radius() < (this.externalRadius() + other.externalRadius());
    }
    
    // TODO: fix bugs in this implementation. Until then use isExternalCircleOverlapping
//  public boolean isOverlapping(Rectangle rect) {
//      return this.hasCornerInOther(rect) || rect.hasCornerInOther(this);
//  }
//    private boolean hasCornerInOther(Rectangle rect)
//    {
//        for (Point2D corner : this.getCorners()) {
//            if (rect.contains(corner)) {
//                return true;
//            }
//        }
//        return false;
//    }
    
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
}