package com.project.canvas.client.shared.searchProviders;

public interface ImageResult extends ImageInfo
{
    String getUrl();
    String getTitle();
    
    Thumbnail getThumbnail();
}
