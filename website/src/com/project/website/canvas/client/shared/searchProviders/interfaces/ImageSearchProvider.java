package com.project.website.canvas.client.shared.searchProviders.interfaces;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.project.website.canvas.client.shared.searchProviders.ImageSearchOptions;

public interface ImageSearchProvider extends MediaSearchProvider
{
    void search(String query, ImageSearchOptions searchOptions, 
            final AsyncCallback<MediaSearchResult> callback);
}
