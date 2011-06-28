package com.project.website.canvas.client.shared.searchProviders;

import com.project.website.canvas.client.shared.searchProviders.interfaces.MediaInfo;

public class MediaInfoImpl implements MediaInfo 
{
    private String mediaUrl = "";
    private int width = 0;
    private int height = 0;
    private String sizeDescription = "";
    
    public MediaInfoImpl(String mediaUrl, String sizeDescription, int width, int height)
    {
        this.mediaUrl = mediaUrl;
        this.sizeDescription = sizeDescription;
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
    
    @Override
    public String getSizeDescription() {
        return this.sizeDescription;
    }

}
