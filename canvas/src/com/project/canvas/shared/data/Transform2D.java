package com.project.canvas.shared.data;

import java.io.Serializable;

import com.vercer.engine.persist.annotation.Embed;

public class Transform2D implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Embed
    public Point2D translation;
    @Embed
	public Point2D size; // could be "null" which means no specific size is set.
    public int rotation; // degrees, around the center of the object

    public Transform2D(Point2D translation, Point2D size, int rotation) {
		this.translation = translation;
		this.size = size;
		this.rotation = rotation;
	}
    public Transform2D() {
    	this(Point2D.zero, Point2D.zero, 0);
    }
}
