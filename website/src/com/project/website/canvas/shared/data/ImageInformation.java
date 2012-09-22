package com.project.website.canvas.shared.data;

import java.io.Serializable;

import com.google.appengine.api.datastore.Blob;
import com.google.code.twig.annotation.Type;
import com.google.common.base.Objects;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.project.shared.data.Point2D;
import com.project.shared.interfaces.ICloneable;


public class ImageInformation implements Serializable, IsSerializable, ICloneable<ImageInformation> {
    private static final long serialVersionUID = 1L;
    
    @Type(Blob.class)
    public String urlData = null;
    
    public String url = "";
    public Point2D size = new Point2D();
    public ImageOptions options = new ImageOptions();

    public ImageInformation() {}

    public ImageInformation(ImageInformation imageInformation)
    {
        this();
        this.url = imageInformation.url;
        this.urlData = imageInformation.urlData;
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
    
    public void setUrl(String url)
    {
    	if (url.trim().startsWith("data"))
    	{
    		this.url = null;
    		this.urlData = url;
    	}
    	else {
    		this.url = url;
    		this.urlData = null;
    	}
    }
    public String getUrl()
    {
    	return (null != this.url) ? this.url : this.urlData;
    }
}
