package com.project.website.canvas.client.shared.searchProviders;

public class ImageInfoImpl extends MediaInfoImpl
{
    public ImageInfoImpl(String mediaUrl, int width, int height) {
        super(mediaUrl, formatSize(width, height), width, height);
    }
    
    private static String formatSize(int width, int height)
    {
        return Integer.toString(width) + " x " + Integer.toString(height);
    }
}
