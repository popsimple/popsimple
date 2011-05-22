package com.project.canvas.client.canvastools.Image.BingImage.BingSearch;

import com.google.gwt.http.client.URL;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class BingSearchProvider 
{
    private static final String PROTOCOL_HTTP = "http";
    private static final String BING_JSON_SEARCH_URL = "api.bing.net/json.aspx";
    
    private static final String PARAMETER_APP_ID = "AppId";
    private static final String PARAMETER_QUERY = "Query";
    private static final String PARAMETER_SOURCES = "Sources";
    private static final String PARAMETER_JSON_TYPE = "JsonType";
    private static final String PARAMETER_CALLBACK = "JsonCallback";
    private static final String PARAMETER_IMAGE_COUNT = "Image.Count";
    
    private static final String SOURCES_IMAGES = "Image";
    private static final String JSON_TYPE_CALLBACK = "callback";
    private static final int CALLBACK_TIMEOUT_MS = 1000;
    
    protected String appID = "";
    
    public BingSearchProvider(String appID)
    {
        this.appID = appID;
    }
    
    public void searchImages(String query, final AsyncCallback<ImageResponse> callback)
    {
        UrlBuilder urlBuilder = new UrlBuilder();
        urlBuilder.setProtocol(PROTOCOL_HTTP);
        urlBuilder.setHost(BING_JSON_SEARCH_URL);
        urlBuilder.setParameter(PARAMETER_APP_ID, this.appID);
        urlBuilder.setParameter(PARAMETER_QUERY, query);
        urlBuilder.setParameter(PARAMETER_SOURCES, SOURCES_IMAGES);
        urlBuilder.setParameter(PARAMETER_JSON_TYPE, JSON_TYPE_CALLBACK);
        urlBuilder.setParameter(PARAMETER_IMAGE_COUNT, "50");
        
        JsonpRequestBuilder requestBuilder = new JsonpRequestBuilder();
        requestBuilder.setCallbackParam(PARAMETER_CALLBACK);
        requestBuilder.setTimeout(CALLBACK_TIMEOUT_MS);
        requestBuilder.requestObject(urlBuilder.buildString(), new AsyncCallback<SearchResult>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(SearchResult result) {
                if (null == result)
                {
                    //TODO: Replace with proper exception.
                    callback.onFailure(new Exception("Null result"));
                    return;
                }
                callback.onSuccess(result.getSearchResponse().getImageResponse());
            }});
    }
}
