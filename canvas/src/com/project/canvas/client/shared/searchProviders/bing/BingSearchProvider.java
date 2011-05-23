package com.project.canvas.client.shared.searchProviders.bing;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.project.canvas.client.shared.searchProviders.ImageSearchOptions;
import com.project.canvas.client.shared.searchProviders.bing.adapters.ImageResponseToToImageSearchAdapter;
import com.project.canvas.client.shared.searchProviders.interfaces.ImageSearchProvider;
import com.project.canvas.client.shared.searchProviders.interfaces.ImageSearchResult;
import com.project.gwtbing.client.ImageSearch.BingImageSearchRequest;
import com.project.gwtbing.client.ImageSearch.ImageResponse;

public class BingSearchProvider implements ImageSearchProvider 
{
    private BingImageSearchRequest imageSearchRequest = null;

    public BingSearchProvider(String appId)
    {
        this.imageSearchRequest = new BingImageSearchRequest(appId);
    }
    
    @Override
    public void search(String query, final AsyncCallback<ImageSearchResult> callback) 
    {
        this.imageSearchRequest.searchImages(query, new AsyncCallback<ImageResponse>() {
            
            @Override
            public void onSuccess(ImageResponse result) {
                callback.onSuccess(new ImageResponseToToImageSearchAdapter(
                        BingImageSearchRequest.DEFAULT_IMAGE_COUNT, result));
            }
            
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);                
            }
        });
    }

    @Override
    public void search(String query, ImageSearchOptions searchOptions,
            AsyncCallback<ImageSearchResult> callback) 
    {
        //TODO: Support ImageSearchOptions
        this.search(query, callback);
    }

}
