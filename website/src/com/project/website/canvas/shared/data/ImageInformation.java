package com.project.website.canvas.shared.data;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.project.shared.data.Point2D;
import com.project.shared.interfaces.ICloneable;
import com.project.shared.utils.CloneableUtils;
import com.project.shared.utils.ObjectUtils;


public class ImageInformation implements Serializable, IsSerializable, ICloneable {
    private static final long serialVersionUID = 1L;

    public String url = "";
    public Point2D size = new Point2D();
    public ImageOptions options = new ImageOptions();

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (false == (obj instanceof ImageInformation)){
            return false;
        }
        ImageInformation other = (ImageInformation)obj;
        if (false == ObjectUtils.areEqual(this.url, other.url)){
            return false;
        }
        if (false == ObjectUtils.areEqual(this.size, other.size)){
            return false;
        }
        if (false == ObjectUtils.areEqual(this.options, other.options))
        {
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
        copy.options = (ImageOptions)CloneableUtils.clone(this.options);
    }
}
