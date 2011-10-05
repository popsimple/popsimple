package com.project.shared.data;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.project.shared.interfaces.ICloneable;


public class Rectangle implements ICloneable<Rectangle>, Serializable, IsSerializable {
    public double getRotation()
    {
        return rotation;
    }

    public void setRotation(double rotation)
    {
        this.rotation = rotation;
    }

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public static final Rectangle empty = new Rectangle(0, 0, 0, 0);

    private int left = 0;
    private int top = 0;
    private int right = 0;
    private int bottom = 0;
    /* Rotation axis is center */
    private double rotation = 0;

    public Rectangle() {
    	this(0, 0, 0, 0);
    }

    public Rectangle(int left, int top, int right, int bottom) {
        this(left, top, right, bottom, 0);
    }

    public Rectangle(int left, int top, int size) {
        this(left, top, left + size, top + size);
    }

    public Rectangle(int left, int top, int right, int bottom, double rotation) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.rotation = rotation;
    }

    public Rectangle(Rectangle rectangle)
    {
        this.left = rectangle.left;
        this.top = rectangle.top;
        this.right = rectangle.right;
        this.bottom = rectangle.bottom;
        this.rotation = rectangle.rotation;
    }

    public boolean contains(Point2D point) {
    	Point2D rotatedPoint = point.getRotated(-Math.toRadians(rotation), getCenter(), true);
    	int px = rotatedPoint.getX();
    	int py = rotatedPoint.getY();
    	return ((px >= left) && (px <= right) && (py <= bottom) && (py >= top));
    }

    public Point2D getCenter() {
        return new Point2D((left + right) / 2, (top + bottom) / 2);
    }


    public class Corners {
        public Corners(Point2D topRight, Point2D bottomRight, Point2D bottomLeft, Point2D topLeft)
        {
            this.topRight = topRight;
            this.bottomRight = bottomRight;
            this.bottomLeft = bottomLeft;
            this.topLeft = topLeft;
        }

        public Point2D[] asArray()
        {
            return new Point2D[] { this.topRight, this.bottomRight, this.bottomLeft, this.topLeft };
        }

        public final Point2D topRight;
        public final Point2D bottomRight;
        public final Point2D bottomLeft;
        public final Point2D topLeft;
    }

    /**
     * @return the actual positions of the corners taking rotation of the rectangle into account.
     */
    public Corners getCorners() {
        Point2D[] corners = new Point2D[] {
            new Point2D(right, top),
            new Point2D(right, bottom),
            new Point2D(left, bottom),
            new Point2D(left, top)
        };
        for (int i = 0; i < corners.length; i++) {
            corners[i] = corners[i].getRotated(Math.toRadians(rotation), getCenter(), true);
        }
        return new Corners(corners[0], corners[1], corners[2], corners[3]);
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
        return corner.minus(getCenter()).getRadius();
    }

    /**
     * @param other rectangle to compare with
     * @return Whether or not the smallest circles containing each of the two rectangles are overlapping
     */
    public boolean isExternalCircleOverlapping(Rectangle other)
    {
        return this.getCenter().minus(other.getCenter()).getRadius() < (this.externalRadius() + other.externalRadius());
    }

    public boolean isOverlapping(Rectangle rect)
    {
        return this.hasCornerInOther(rect) || rect.hasCornerInOther(this);
    }

    private boolean hasCornerInOther(Rectangle rect)
    {
        for (Point2D corner : this.getCorners().asArray()) {
            if (rect.contains(corner)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object other) {
        if (null == other) {
            return false;
        }
        if (other.getClass() != this.getClass()) {
            return false;
        }
        Rectangle otherRectangle = (Rectangle)other;
        return ((this.bottom == otherRectangle.bottom) &&
                (this.left == otherRectangle.left) &&
                (this.right == otherRectangle.right) &&
                (this.top == otherRectangle.top ) &&
                (this.rotation == otherRectangle.rotation));
    }

    @Override
    public Rectangle getClone()
    {
        return new Rectangle(this);
    }

//    public boolean isOverlapping(Rectangle rect) {
//        if (this.right < rect.left) {
//            return false;
//        }
//        if (this.left > rect.right) {
//            return false;
//        }
//        if (this.bottom < rect.top) {
//            return false;
//        }
//        if (this.top > rect.bottom) {
//            return false;
//        }
//        return true;
//    }
}
