package com.project.canvas.client.shared.searchProviders.interfaces;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ImageResult 
{
    String getUrl();
    String getTitle();
    
    void getImageSizes(final AsyncCallback<ArrayList<ImageInfo>> callback);
    
    String getThumbnailUrl();
}
