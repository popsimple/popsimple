package com.project.canvas.client.shared.searchProviders.youtube;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.project.canvas.client.resources.CanvasResources;
import com.project.canvas.client.shared.searchProviders.ImageSearchOptions;
import com.project.canvas.client.shared.searchProviders.interfaces.ImageSearchProvider;
import com.project.canvas.client.shared.searchProviders.interfaces.ImageSearchResult;
import com.project.canvas.client.shared.searchProviders.youtube.adapters.YouTubeResultToImageSearchAdapter;

public class YouTubeSearchProvider implements ImageSearchProvider 
{
    @Override
    public void search(String query, final AsyncCallback<ImageSearchResult> callback) {
        UrlBuilder urlBuilder = new UrlBuilder();
        urlBuilder.setProtocol("HTTP");
        urlBuilder.setHost("gdata.youtube.com");
        urlBuilder.setPath("feeds/api/videos");
        urlBuilder.setParameter("alt", "jsonc");
        urlBuilder.setParameter("v", "2");
        urlBuilder.setParameter("q", query);
        
        JsonpRequestBuilder requestBuilder = new JsonpRequestBuilder();
        requestBuilder.setCallbackParam("callback");
        requestBuilder.requestObject(urlBuilder.buildString(), new AsyncCallback<YouTubeResult>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(YouTubeResult result) {
                callback.onSuccess(new YouTubeResultToImageSearchAdapter(result));
            }
        });
    }

    @Override
    public void search(String query, ImageSearchOptions searchOptions,
            AsyncCallback<ImageSearchResult> callback) {
        this.search(query, callback);
    }

    @Override
    public String getTitle() {
        return "YouTube";
    }

    @Override
    public String getIconUrl() {
        return CanvasResources.INSTANCE.youtubeLogo32().getURL();
    }

}
