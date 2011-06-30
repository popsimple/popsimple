package com.project.website.canvas.client.shared.searchProviders.flickr;

import com.ghusse.dolomite.flickr.Credentials;
import com.ghusse.dolomite.flickr.PhotosResponse;
import com.ghusse.dolomite.flickr.photos.Search;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.project.website.canvas.client.resources.CanvasResources;
import com.project.website.canvas.client.shared.searchProviders.ImageSearchOptions;
import com.project.website.canvas.client.shared.searchProviders.flickr.adapters.PhotosResponseToImageSearchAdapter;
import com.project.website.canvas.client.shared.searchProviders.interfaces.ImageSearchProvider;
import com.project.website.canvas.client.shared.searchProviders.interfaces.MediaSearchResult;

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
    public void search(String query, final AsyncCallback<MediaSearchResult> callback) 
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
            final AsyncCallback<MediaSearchResult> callback) 
    {
        //TODO: Support search options.
        this.search(query, callback);
    }

    @Override
    public String getTitle() {
        return "Flickr";
    }

    @Override
    public String getIconUrl() {
        return CanvasResources.INSTANCE.flickrLogo32().getURL();
    }
}
