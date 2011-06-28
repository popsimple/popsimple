package com.project.canvas.shared.data;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.project.shared.data.ICloneable;

public class ImageOptions implements Serializable, IsSerializable, ICloneable {
    private static final long serialVersionUID = 1L;

    public boolean repeat = false;
    public boolean center = false;
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
        if (this.center != other.center){
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

    @Override
    public Object createInstance() {
        return new ImageOptions();
    }

    @Override
    public void copyTo(Object object) {
        ImageOptions copy = (ImageOptions)object;

        copy.repeat = this.repeat;
        copy.center = this.center;
        copy.stretchWidth = this.stretchWidth;
        copy.stretchHeight = this.stretchHeight;
        copy.useOriginalSize = this.useOriginalSize;
    }
}
