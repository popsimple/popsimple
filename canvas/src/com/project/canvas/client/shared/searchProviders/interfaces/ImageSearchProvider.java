package com.project.canvas.client.shared.searchProviders.interfaces;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.project.canvas.client.shared.searchProviders.ImageSearchOptions;

public interface ImageSearchProvider 
{
    void search(String query, final AsyncCallback<ImageSearchResult> callback);
    void search(String query, ImageSearchOptions searchOptions, 
            final AsyncCallback<ImageSearchResult> callback);
}
