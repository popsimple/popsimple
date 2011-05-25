package com.project.gwtyoutube.client;

import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class YouTubeSearchRequest 
{
    private static final String PROTOCOL_HTTP = "http";
    private static final String YOUTUBE_SEARCH_API_HOST = "gdata.youtube.com";
    private static final String YOUTUBE_SEARCH_PATH = "feeds/api/videos";
    
    public void search(String query, final AsyncCallback<YouTubeResult> callback)
    {
        this.search(query, callback, new HashMap<String, String>());
    }
    
    public void search(String query, final AsyncCallback<YouTubeResult> callback,
            HashMap<String, String> additionalParams)
    {
        String searchUrl = this.buildSearchUrl(query, additionalParams);
        
        JsonpRequestBuilder requestBuilder = new JsonpRequestBuilder();
        requestBuilder.setCallbackParam(SearchParameters.CALLBACK);
        requestBuilder.requestObject(searchUrl, new AsyncCallback<YouTubeResult>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(YouTubeResult result) {
                if (null == result)
                {
                    //TODO: Replace with proper exception.
                    callback.onFailure(new YouTubeSearchNullResultException());
                    return;
                }
                callback.onSuccess(result);
            }});
    }
    
    private String buildSearchUrl(String query, HashMap<String, String> additionalParams)
    {
        UrlBuilder urlBuilder = new UrlBuilder();
        urlBuilder.setProtocol(PROTOCOL_HTTP);
        urlBuilder.setHost(YOUTUBE_SEARCH_API_HOST);
        urlBuilder.setPath(YOUTUBE_SEARCH_PATH);
        urlBuilder.setParameter(SearchParameters.RETURN_TYPE, SearchReturnTypes.JSON_COMPACT);
        urlBuilder.setParameter(SearchParameters.VERSION, YouTubeVersions.TWO);
        urlBuilder.setParameter(SearchParameters.SUPPORTED_FORMATS, YouTubeFormats.EMBEDDED); 
        urlBuilder.setParameter(SearchParameters.QUERY, query);
        
        for (Entry<String, String> additionalParam : additionalParams.entrySet())
        {
            urlBuilder.setParameter(additionalParam.getKey(), additionalParam.getValue());
        }
        return urlBuilder.buildString();
    }
}
