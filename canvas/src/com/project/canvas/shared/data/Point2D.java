package com.project.canvas.shared.data;

import java.io.Serializable;

public class Point2D implements Serializable {
	private static final long serialVersionUID = 1L;

	private final int x;
	private final int y;

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
}
