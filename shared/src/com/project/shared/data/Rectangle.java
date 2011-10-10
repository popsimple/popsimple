package com.project.shared.data;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.project.shared.interfaces.ICloneable;
import com.project.shared.utils.PointUtils;


public class Rectangle implements ICloneable<Rectangle>, Serializable, IsSerializable {
    public class Corners {
        public final Point2D topRight;
        public final Point2D bottomRight;
        public final Point2D bottomLeft;
        public final Point2D topLeft;
        
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

    public Rectangle(int left, int top, int size) {
        this(left, top, left + size, top + size);
    }

    public Rectangle(int left, int top, int right, int bottom) {
        this(left, top, right, bottom, 0);
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
        this(rectangle.getLeft(), rectangle.getTop(),
             rectangle.getRight(), rectangle.getBottom(), rectangle.getRotation());
    }

    public Rectangle(Point2D topLeft, Point2D bottomRight)
    {
        this(topLeft.getX(), topLeft.getY(),
             bottomRight.getX(), bottomRight.getY());
    }

    public boolean contains(Point2D point) {
    	// We compare by rotating the point to axis of the rectangle.
    	// An alternative implementation using crossing number or winding number may be more efficient
    	// see http://softsurfer.com/Archive/algorithm_0103/algorithm_0103.htm
    	Point2D rotatedPoint = point.getRotated(-Math.toRadians(rotation), getCenter());
    	int px = rotatedPoint.getX();
    	int py = rotatedPoint.getY();
    	return ((px >= left) && (px <= right) && (py <= bottom) && (py >= top));
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

    /**
     * @return radius of smallest circle containing the rectangle
     */
    public double externalRadius()
    {
        Point2D corner = new Point2D(right, top);
        return corner.minus(getCenter()).getRadius();
    }


    public int getBottom()
    {
    	return this.bottom;
    }

    public Point2D getCenter() {
        return new Point2D((left + right) / 2, (top + bottom) / 2);
    }

    @Override
    public Rectangle getClone()
    {
        return new Rectangle(this);
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
            corners[i] = corners[i].getRotated(Math.toRadians(rotation), getCenter());
        }
        return new Corners(corners[0], corners[1], corners[2], corners[3]);
    }

    public int getLeft()
    {
    	return this.left;
    }

    public int getRight()
    {
    	return this.right;
    }

    public double getRotation()
    {
        return rotation;
    }

    public Point2D getSize()
    {
    	return new Point2D(
    			Math.abs((this.right - this.left)),
    			Math.abs((this.bottom - this.top)));
    }

    public int getTop()
    {
    	return this.top;
    }

    /**
     * @param other rectangle to compare with
     * @return Whether or not the smallest circles containing each of the two rectangles are overlapping
     */
    public boolean isExternalCircleOverlapping(Rectangle other)
    {
        return this.getCenter().minus(other.getCenter()).getRadius() < (this.externalRadius() + other.externalRadius());
    }

    /**
     * Returns true of the two rectangles overlap.
     * @See <a href="http://stackoverflow.com/questions/115426/algorithm-to-detect-intersection-of-two-rectangles">http://stackoverflow.com/questions/115426/algorithm-to-detect-intersection-of-two-rectangles</a>
     */
    public boolean isOverlapping(Rectangle rect)
    {
    	Point2D[] myCorners = this.getCorners().asArray();
    	Point2D[] myEdges = PointUtils.getEdgeVectors(myCorners);
    	
    	Point2D[] otherCorners = rect.getCorners().asArray();
    	Point2D[] otherEdges = PointUtils.getEdgeVectors(otherCorners);
    	
    	int numCorners = myCorners.length;
		for (int i = 0; i < numCorners; i++) {
    		if (isSeparatingEdge(myCorners, myEdges, otherCorners, i)) {
    			return false;
    		}
    		if (isSeparatingEdge(otherCorners, otherEdges, myCorners, i)) {
    			return false;
    		}
    	}
		return true;
    }

	private static boolean isSeparatingEdge(Point2D[] myCorners, Point2D[] myEdges, Point2D[] otherCorners, int i) 
	{
		int numCorners = myCorners.length;
		Point2D edgeNormal = myEdges[i].getRotatedBy90Deg();
		Point2D cornerOnOpposingEdge = myCorners[(i + 2) % numCorners];
		Point2D edgeVertex = myCorners[i];
		// Check what side of the edge a vertex of this rect belongs to
		int mySide = getSideOfPointOnEdge(edgeVertex, edgeNormal, cornerOnOpposingEdge);
		// Check the first corner of the other rect to see its side
		int otherRectSide = getSideOfPointOnEdge(edgeVertex, edgeNormal, otherCorners[0]);
		if (mySide == otherRectSide) {
			// the edge is NOT a separating line, because one of our vertices
			// and the other rect's vertices are on the same side.
			return false;
		}
		for (int j = 1; j < numCorners; j++) {
			if (otherRectSide != getSideOfPointOnEdge(edgeVertex, edgeNormal, otherCorners[j])) {
				// the edge is NOT a separating line, because two vertices of the other rect 
				// are on two different sides of the edge.
				return false;
			}
		}
		return true;
	}

    /**
     * Returns the side of a point relative to the given edge. 
     */
	private static int getSideOfPointOnEdge(Point2D edgeVertex, Point2D edgeNormal, Point2D testPoint) {
		return Integer.signum(edgeNormal.dotProduct(testPoint.minus(edgeVertex)));
	}

    public Rectangle move(Point2D target)
    {
        int newRight = this.right - (this.left - target.getX());
        int newBottom = this.bottom - (this.top - target.getY());

        return new Rectangle(target.getX(), target.getY(), newRight, newBottom);
    }

    public void setBottom(int bottom)
    {
    	this.bottom = bottom;
    }

    public void setLeft(int left)
    {
    	this.left = left;
    }

    public void setRight(int right)
    {
    	this.right = right;
    }

    public void setRotation(double rotation)
    {
        this.rotation = rotation;
    }

    public void setTop(int top)
    {
    	this.top = top;
    }
}
