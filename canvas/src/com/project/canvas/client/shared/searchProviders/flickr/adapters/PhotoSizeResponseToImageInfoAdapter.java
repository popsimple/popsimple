package com.project.canvas.client.shared.searchProviders.flickr.adapters;

import com.ghusse.dolomite.flickr.PhotoSizesResponse.PhotoSizeResponse;
import com.project.canvas.client.shared.searchProviders.interfaces.ImageInfo;

public class PhotoSizeResponseToImageInfoAdapter implements ImageInfo 
{
    PhotoSizeResponse sizeResponse = null;
    
    public PhotoSizeResponseToImageInfoAdapter(PhotoSizeResponse sizeResponse)
    {
        this.sizeResponse = sizeResponse;
    }

    @Override
    public String getMediaUrl() 
    {
        return this.sizeResponse.getSource();
    }

    @Override
    public int getWidth() {
        return this.sizeResponse.getWidth();
    }

    @Override
    public int getHeight() {
        return this.sizeResponse.getHeight();
    }

}
