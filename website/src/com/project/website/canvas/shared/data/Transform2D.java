package com.project.website.canvas.shared.data;

import java.io.Serializable;

import com.google.code.twig.annotation.Embedded;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.project.shared.data.Point2D;

public class Transform2D implements Serializable, IsSerializable {
    private static final long serialVersionUID = 1L;

    @Embedded
    public Point2D translation = new Point2D();
    @Embedded
	public Point2D size; // could be "null" which means no specific size is set.

    public double rotation; // degrees, around the center of the object

    public Transform2D() {}

    public Transform2D(Point2D translation, Point2D size, double rotation) {
        this();
		this.translation = translation;
		this.size = size;
		this.rotation = rotation;
	}

    public Transform2D(Transform2D other) {
        this();
        this.translation = other.translation.getClone();
        this.size = other.size.getClone();
        this.rotation = other.rotation;
    }

}
