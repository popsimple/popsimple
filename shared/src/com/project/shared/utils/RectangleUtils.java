package com.project.shared.utils;

import com.project.shared.data.Point2D;
import com.project.shared.data.Rectangle;

public class RectangleUtils
{
	public final static Rectangle Build(Point2D corner1, Point2D corner2)
	{
		Rectangle rectangle = new Rectangle();

		if (corner1.getX() <= corner2.getX())
		{
			rectangle.setLeft(corner1.getX());
			rectangle.setRight(corner2.getX());
		}
		else
		{
			rectangle.setLeft(corner2.getX());
			rectangle.setRight(corner1.getX());
		}
		if (corner1.getY() <= corner2.getY())
		{
			rectangle.setTop(corner1.getY());
			rectangle.setBottom(corner2.getY());
		}
		else
		{
			rectangle.setTop(corner2.getY());
			rectangle.setBottom(corner1.getY());
		}
		return rectangle;
	}
}
