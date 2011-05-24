package com.project.canvas.client.shared.searchProviders.interfaces;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MediaResult 
{
    String getUrl();
    String getTitle();
    
    void getMediaSizes(final AsyncCallback<ArrayList<MediaInfo>> callback);
    
    String getThumbnailUrl();
}
