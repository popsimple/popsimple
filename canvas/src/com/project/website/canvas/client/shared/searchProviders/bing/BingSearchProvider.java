package com.project.website.canvas.client.shared.searchProviders.bing;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.project.gwtbing.client.imagesearch.BingImageSearchRequest;
import com.project.gwtbing.client.imagesearch.ImageResponse;
import com.project.website.canvas.client.resources.CanvasResources;
import com.project.website.canvas.client.shared.searchProviders.ImageSearchOptions;
import com.project.website.canvas.client.shared.searchProviders.bing.adapters.ImageResponseToToImageSearchAdapter;
import com.project.website.canvas.client.shared.searchProviders.interfaces.ImageSearchProvider;
import com.project.website.canvas.client.shared.searchProviders.interfaces.MediaSearchResult;

public class BingSearchProvider implements ImageSearchProvider 
{
    private BingImageSearchRequest imageSearchRequest = null;

    public BingSearchProvider(String appId)
    {
        this.imageSearchRequest = new BingImageSearchRequest(appId);
    }
    
    @Override
    public void search(String query, final AsyncCallback<MediaSearchResult> callback) 
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
            AsyncCallback<MediaSearchResult> callback) 
    {
        //TODO: Support ImageSearchOptions
        this.search(query, callback);
    }

    @Override
    public String getTitle() {
        return "Bing";
    }

    @Override
    public String getIconUrl() {
        return CanvasResources.INSTANCE.bingLogo32().getURL();
    }

}
