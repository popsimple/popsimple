package com.project.website.canvas.client.shared.searchProviders.youtube;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.project.gwtyoutube.client.YouTubeResult;
import com.project.gwtyoutube.client.YouTubeSearchRequest;
import com.project.website.canvas.client.resources.CanvasResources;
import com.project.website.canvas.client.shared.searchProviders.interfaces.MediaSearchResult;
import com.project.website.canvas.client.shared.searchProviders.interfaces.VideoSearchProvider;
import com.project.website.canvas.client.shared.searchProviders.youtube.adapters.YouTubeResultAdapter;

public class YouTubeSearchProvider implements VideoSearchProvider 
{
    private YouTubeSearchRequest searchRequest = new YouTubeSearchRequest(); 
    
    @Override
    public void search(String query, final AsyncCallback<MediaSearchResult> callback) {
        searchRequest.search(query, new AsyncCallback<YouTubeResult>() {
            
            @Override
            public void onSuccess(YouTubeResult result) {
                callback.onSuccess(new YouTubeResultAdapter(result));
            }
            
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
                
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
