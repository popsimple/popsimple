package com.project.canvas.client.shared.searchProviders;

import com.project.canvas.client.shared.searchProviders.interfaces.ImageInfo;

public class ImageInfoImpl implements ImageInfo 
{
    private String mediaUrl = "";
    private int width = 0;
    private int height = 0;
    
    public ImageInfoImpl(String mediaUrl, int width, int height)
    {
        this.mediaUrl = mediaUrl;
        this.width = width;
        this.height = height;
    }
    
    @Override
    public String getMediaUrl() {
        // TODO Auto-generated method stub
        return this.mediaUrl;
    }

    @Override
    public int getWidth() {
        // TODO Auto-generated method stub
        return this.width;
    }

    @Override
    public int getHeight() {
        // TODO Auto-generated method stub
        return this.height;
    }

}
