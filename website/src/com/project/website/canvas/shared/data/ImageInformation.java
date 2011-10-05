package com.project.website.canvas.shared.data;

import java.io.Serializable;

import com.google.common.base.Objects;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.project.shared.data.Point2D;
import com.project.shared.interfaces.ICloneable;


public class ImageInformation implements Serializable, IsSerializable, ICloneable<ImageInformation> {
    private static final long serialVersionUID = 1L;

    public String url = "";
    public Point2D size = new Point2D();
    public ImageOptions options = new ImageOptions();

    public ImageInformation() {}

    public ImageInformation(ImageInformation imageInformation)
    {
        this();
        this.url = imageInformation.url;
        this.size = imageInformation.size.getClone();
        this.options = imageInformation.options.getClone();

    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (false == (obj instanceof ImageInformation)){
            return false;
        }
        ImageInformation other = (ImageInformation)obj;
        if (false == Objects.equal(this.url, other.url)){
            return false;
        }
        if (false == Objects.equal(this.size, other.size)){
            return false;
        }
        if (false == Objects.equal(this.options, other.options))
        {
            return false;
        }
        return true;
    }

    @Override
    public ImageInformation getClone()
    {
        return new ImageInformation(this);
    }
}
