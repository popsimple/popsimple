package com.project.website.canvas.shared.data;

import java.io.Serializable;

import com.google.code.twig.annotation.Embedded;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.project.shared.data.Point2D;
import com.project.shared.interfaces.ICloneable;

public class Transform2D implements Serializable, IsSerializable, ICloneable {
    private static final long serialVersionUID = 1L;

    @Embedded
    public Point2D translation = new Point2D();
    @Embedded
	public Point2D size; // could be "null" which means no specific size is set.

    public double rotation; // degrees, around the center of the object

    public Transform2D(Point2D translation, Point2D size, double rotation) {
		this.translation = translation;
		this.size = size;
		this.rotation = rotation;
	}
    public Transform2D() {}

    @Override
	public Object createInstance() {
		return new Transform2D();
	}

    @Override
	public void copyTo(Object object) {
    	Transform2D copy = (Transform2D)object;
    	copy.translation = new Point2D(this.translation.getX(), this.translation.getY());
		if (null != this.size)
		{
			copy.size = new Point2D(this.size.getX(), this.size.getY());
		}
		copy.rotation = this.rotation;
	}
}
