package com.project.canvas.client.shared.searchProviders;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ImageSearchProvider 
{
    void search(String query, AsyncCallback<ImageSearchResult> callback);
    void search(String query, ImageSearchOptions searchOptions, 
            AsyncCallback<ImageSearchResult> callback);
}
