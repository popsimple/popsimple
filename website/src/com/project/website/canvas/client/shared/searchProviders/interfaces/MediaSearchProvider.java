package com.project.website.canvas.client.shared.searchProviders.interfaces;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MediaSearchProvider
{
    void search(String query, final AsyncCallback<MediaSearchResult> callback);
    
    String getTitle();
    String getIconUrl();
}
