package com.project.canvas.client.shared.searchProviders.youtube;

import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.project.canvas.client.resources.CanvasResources;
import com.project.canvas.client.shared.searchProviders.interfaces.MediaSearchProvider;
import com.project.canvas.client.shared.searchProviders.interfaces.MediaSearchResult;
import com.project.canvas.client.shared.searchProviders.interfaces.VideoSearchProvider;
import com.project.canvas.client.shared.searchProviders.youtube.adapters.YouTubeResultAdapter;

public class YouTubeSearchProvider implements VideoSearchProvider 
{
    @Override
    public void search(String query, final AsyncCallback<MediaSearchResult> callback) {
        UrlBuilder urlBuilder = new UrlBuilder();
        urlBuilder.setProtocol("HTTP");
        urlBuilder.setHost("gdata.youtube.com");
        urlBuilder.setPath("feeds/api/videos");
        urlBuilder.setParameter("alt", "jsonc");
        urlBuilder.setParameter("v", "2");
        urlBuilder.setParameter("format", "5"); // only embeddable videos.
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
                callback.onSuccess(new YouTubeResultAdapter(result));
            }
        });
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
