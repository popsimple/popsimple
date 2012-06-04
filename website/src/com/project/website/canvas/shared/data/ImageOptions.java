package com.project.website.canvas.shared.data;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.project.shared.interfaces.ICloneable;

public class ImageOptions implements Serializable, IsSerializable, ICloneable<ImageOptions> {
    private static final long serialVersionUID = 1L;

    public boolean repeat = false;
    public boolean centerX = false;
    public boolean centerY = false;
    public boolean stretchWidth = false;
    public boolean stretchHeight = false;
    public boolean useOriginalSize = false;

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (false == (obj instanceof ImageOptions)){
            return false;
        }
        ImageOptions other = (ImageOptions)obj;
        if (this.repeat != other.repeat){
            return false;
        }
        if (this.centerX != other.centerX){
            return false;
        }
        if (this.centerY != other.centerY){
            return false;
        }
        if (this.stretchWidth != other.stretchWidth){
            return false;
        }
        if (this.stretchHeight != other.stretchHeight){
            return false;
        }
        if (this.useOriginalSize != other.useOriginalSize){
            return false;
        }
        return true;
    }

    public ImageOptions() {}

    public ImageOptions(ImageOptions other)
    {
        this();
        this.repeat = other.repeat;
        this.centerX = other.centerX;
        this.centerY = other.centerY;
        this.stretchWidth = other.stretchWidth;
        this.stretchHeight = other.stretchHeight;
        this.useOriginalSize = other.useOriginalSize;
    }

    @Override
    public ImageOptions getClone()
    {
        return new ImageOptions(this);
    }
}
