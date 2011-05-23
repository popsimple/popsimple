package com.project.gwtbing.client.ImageSearch;

import java.util.HashMap;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.project.gwtbing.client.BingSearchRequest;
import com.project.gwtbing.client.SearchResponse;
import com.project.gwtbing.client.SearchSources;

public class BingImageSearchRequest extends BingSearchRequest
{
    public static final int DEFAULT_IMAGE_COUNT = 50;
    
    public BingImageSearchRequest(String appId) 
    {
        super(appId);
    }  
    
    public void searchImages(String query, final AsyncCallback<ImageResponse> callback)
    {
        HashMap<String, String> imageParams = new HashMap<String, String>();
        imageParams.put(ImageSearchParameters.IMAGE_COUNT, 
                Integer.toString(DEFAULT_IMAGE_COUNT));
        
        super.jsonSearch(query, SearchSources.IMAGES, imageParams, new AsyncCallback<SearchResponse>() {
            
            @Override
            public void onSuccess(SearchResponse result) 
            {
                callback.onSuccess(result.getImageResponse());
            }
            
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
        });
    }
}
