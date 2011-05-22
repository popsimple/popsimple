package com.project.canvas.client.canvastools.Image.BingImage.BingSearch;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.user.client.Window;
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
    
    private static final String JSON_TYPE_CALLBACK = "callback";
    
    AsyncCallback<ImageResponse> callback = null;
    private static final String SOURCES_IMAGES = "Image";
    
    private int jsonRequestId = 0; 
        
    protected String appID = "";
    
    public BingSearchProvider(String appID)
    {
        this.appID = appID;
    }
    
    public String BuildUniqueCallbackId()
    {
        return "callback" + jsonRequestId++;
    }
    
    public void searchImages(String query, final AsyncCallback<ImageResponse> callback)
    {
        // TEMP
        this.callback = callback;
        UrlBuilder urlBuilder = new UrlBuilder();
        urlBuilder.setProtocol(PROTOCOL_HTTP);
        urlBuilder.setHost(BING_JSON_SEARCH_URL);
        urlBuilder.setParameter(PARAMETER_APP_ID, this.appID);
        urlBuilder.setParameter(PARAMETER_QUERY, query);
        urlBuilder.setParameter(PARAMETER_SOURCES, SOURCES_IMAGES);
        urlBuilder.setParameter(PARAMETER_JSON_TYPE, JSON_TYPE_CALLBACK);
        urlBuilder.setParameter(PARAMETER_IMAGE_COUNT, "50");
        
        String callbackId =  this.BuildUniqueCallbackId();
        urlBuilder.setParameter(PARAMETER_CALLBACK, callbackId);
        
        try
        {
            getJson(callbackId, urlBuilder.buildString(), this);
        }
        catch (Exception ex)
        {
            callback.onFailure(ex);
        }
    }
    
    public native static void getJson(String callbackId, String url, BingSearchProvider handler) /*-{
        var script = document.createElement("script");
        script.setAttribute("src", url);
        script.setAttribute("type", "text/javascript");
        
        window[callbackId] = function(jsonObj) {
            handler.@com.project.canvas.client.canvastools.Image.BingImage.BingSearch.BingSearchProvider::handleJsonResponse(Lcom/google/gwt/core/client/JavaScriptObject;)(jsonObj);
            window[callbackId + "done"] = true;
        }
        setTimeout(function() {
            if (!window[callbackId + "done"]) {
                handler.@com.project.canvas.client.canvastools.Image.BingImage.BingSearch.BingSearchProvider::handleJsonResponse(Lcom/google/gwt/core/client/JavaScriptObject;)(null);
            }
            document.body.removeChild(script);
            delete window[callbackId];
            delete window[callbackId + "done"];
        }, 1000);
        
        document.getElementsByTagName('head')[0].appendChild(script);
    }-*/;
    
    public void handleJsonResponse(JavaScriptObject jsObject) 
    {
        if (null == jsObject) 
        {
          this.callback.onFailure(null);
          return;
        }
        this.callback.onSuccess(((SearchResult)jsObject).getSearchResponse().getImageResponse());
    }
}
