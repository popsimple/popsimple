package com.project.canvas.shared.data;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.project.canvas.shared.CloneableUtils;
import com.project.canvas.shared.ObjectUtils;
import com.project.canvas.shared.contracts.ICloneable;


public class ImageInformation implements Serializable, IsSerializable, ICloneable {
    private static final long serialVersionUID = 1L;

    public String url = "";
    public Point2D size = new Point2D();
    public boolean repeat = false;
    public boolean center = false;
    public boolean stretchWidth = false;
    public boolean stretchHeight = false;

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (false == (obj instanceof ImageInformation)){
            return false;
        }
        ImageInformation other = (ImageInformation)obj;
        if (false == ObjectUtils.equals(this.url, other.url)){
            return false;
        }
        if (false == ObjectUtils.equals(this.size, other.size)){
            return false;
        }
        if (this.repeat != other.repeat){
            return false;
        }
        if (this.center != other.center){
            return false;
        }
        if (this.stretchWidth != other.stretchWidth){
            return false;
        }
        if (this.stretchHeight != other.stretchHeight){
            return false;
        }
        return true;
    }

    @Override
    public Object createInstance() {
        return new ImageInformation();
    }

    @Override
    public void copyTo(Object object) {
        ImageInformation copy = (ImageInformation)object;

        copy.url = this.url;
        copy.size = (Point2D)CloneableUtils.clone(this.size);
        copy.repeat = this.repeat;
        copy.center = this.center;
        copy.stretchWidth = this.stretchWidth;
        copy.stretchHeight = this.stretchHeight;
    }
}
