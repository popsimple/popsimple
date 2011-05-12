package com.project.canvas.shared.data;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Point2D implements Serializable, IsSerializable {
	private static final long serialVersionUID = 1L;

	private int x;
	private int y;

	public Point2D() { }
	
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

	public void setX(int x) { this.x = x; }
	public void setY(int y) { this.y = y; }
}
