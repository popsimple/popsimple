package com.project.canvas.client.shared.searchProviders.flickr;

import com.ghusse.dolomite.flickr.Credentials;
import com.ghusse.dolomite.flickr.PhotosResponse;
import com.ghusse.dolomite.flickr.photos.Search;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.project.canvas.client.shared.searchProviders.ImageSearchOptions;
import com.project.canvas.client.shared.searchProviders.flickr.adapters.PhotosResponseToImageSearchAdapter;
import com.project.canvas.client.shared.searchProviders.interfaces.ImageSearchProvider;
import com.project.canvas.client.shared.searchProviders.interfaces.ImageSearchResult;

public class FlickrSearchProvider implements ImageSearchProvider 
{
    protected Search _searcher = null;
    protected Credentials _credentials = null;
    
    public FlickrSearchProvider(String apiKey)
    {
        this._credentials = new Credentials(apiKey);   
        this._searcher = new Search(_credentials);
    }
    
    @Override
    public void search(String query, final AsyncCallback<ImageSearchResult> callback) 
    {
        this._searcher.setText(query);
        this._searcher.send(new AsyncCallback<PhotosResponse>() {
            
            @Override
            public void onSuccess(PhotosResponse result) 
            {
                if (false == result.getStatus())
                {
                    //TODO: Replace with proper exception.
                    callback.onFailure(new FlickrSearchErrorException(
                        result.getMessage()));
                    return;
                }
                callback.onSuccess(new PhotosResponseToImageSearchAdapter(
                        _credentials,  result));
            }
            
            @Override
            public void onFailure(Throwable caught) 
            {
                callback.onFailure(caught);
            }
        });
    }

    @Override
    public void search(String query, ImageSearchOptions searchOptions,
            final AsyncCallback<ImageSearchResult> callback) 
    {
        //TODO: Support search options.
        this.search(query, callback);
    }
}
