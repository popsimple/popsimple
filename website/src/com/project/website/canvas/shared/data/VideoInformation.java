package com.project.website.canvas.shared.data;

import java.io.Serializable;

import com.google.common.base.Objects;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.project.shared.data.Point2D;

public class VideoInformation implements Serializable, IsSerializable {
    private static final long serialVersionUID = 1L;

    public String url = "";
    public Point2D size = new Point2D();

    public VideoInformation() { }

    public VideoInformation(VideoInformation videoInformation)
    {
        this();
        this.url = videoInformation.url;
        this.size = videoInformation.size.getClone();
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
        if (false == Objects.equal(this.url, other.url)){
            return false;
        }
        if (false == Objects.equal(this.size, other.size)){
            return false;
        }
        return true;
    }
}
