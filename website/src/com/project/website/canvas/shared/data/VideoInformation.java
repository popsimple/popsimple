package com.project.website.canvas.shared.data;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.project.shared.data.Point2D;
import com.project.shared.interfaces.ICloneable;
import com.project.shared.utils.CloneableUtils;
import com.project.shared.utils.ObjectUtils;

public class VideoInformation implements Serializable, IsSerializable, ICloneable {
    private static final long serialVersionUID = 1L;

    public String url = "";
    public Point2D size = new Point2D();

    @Override
    public Object createInstance() {
        return new VideoInformation();
    }
    @Override
    public void copyTo(Object object) {
        VideoInformation copy = (VideoInformation)object;

        copy.url = this.url;
        copy.size = (Point2D)CloneableUtils.clone(this.size);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (false == (obj instanceof VideoInformation)){
            return false;
        }
        VideoInformation other = (VideoInformation)obj;
        if (false == ObjectUtils.areEqual(this.url, other.url)){
            return false;
        }
        if (false == ObjectUtils.areEqual(this.size, other.size)){
            return false;
        }
        return true;
    }
}
