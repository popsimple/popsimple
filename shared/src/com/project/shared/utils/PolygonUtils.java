package com.project.shared.utils;

import com.project.shared.data.Point2D;

public class PolygonUtils {

	/**
	 * Returns whether edge number i in the array of a polygon edges is a
	 * separating edge between two polygons, given the corners of both polygons.
	 * 
	 * <p>
	 * A separating edge is one which divides the plane such that one polygon is
	 * on one side and the other polygon is on the other side.
	 * </p>
	 */
	public static boolean isSeparatingEdge(Point2D[] myCorners, Point2D[] myEdges, Point2D[] otherCorners, int i) 
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


    /**
     * Returns true of the two polygons overlap.
     * @See <a href="http://stackoverflow.com/questions/115426/algorithm-to-detect-intersection-of-two-rectangles">http://stackoverflow.com/questions/115426/algorithm-to-detect-intersection-of-two-rectangles</a>
     */
    public static boolean areOverlapping(Point2D[] myCorners, Point2D[] otherCorners)
    {
    	Point2D[] myEdges = PointUtils.getEdgeVectors(myCorners);
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


}
