package com.project.gwtbing.client;

import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.project.gwtbing.client.options.JsonTypes;

public class BingSearchRequest
{
    private static final String PROTOCOL_HTTP = "http";
    private static final String BING_SEARCH_API_HOST = "api.bing.net";
    private static final String BING_JSON_PATH = "json.aspx";
    
    protected String appID = "";
    
    public BingSearchRequest(String appID)
    {
        this.appID = appID;
    }
    
    public void jsonSearch(String query, String source, final AsyncCallback<SearchResponse> callback)
    {
        this.jsonSearch(query, source, new HashMap<String, String>(), callback);
    }
    
    public void jsonSearch(String query, String source, HashMap<String, String> additionalParams,
            final AsyncCallback<SearchResponse> callback)
    {
        String searchUrl = this.buildSearchUrl(query, source, additionalParams);
        
        JsonpRequestBuilder requestBuilder = new JsonpRequestBuilder();
        requestBuilder.setCallbackParam(SearchParameters.CALLBACK);
        requestBuilder.requestObject(searchUrl, new AsyncCallback<SearchResult>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(SearchResult result) {
                if (null == result)
                {
                    //TODO: Replace with proper exception.
                    callback.onFailure(new BingSearchNullResultException());
                    return;
                }
                callback.onSuccess(result.getSearchResponse());
            }});
    }
    
    private String buildSearchUrl(String query, String source, 
            HashMap<String, String> additionalParams)
    {
        UrlBuilder urlBuilder = new UrlBuilder();
        urlBuilder.setProtocol(PROTOCOL_HTTP);
        urlBuilder.setHost(BING_SEARCH_API_HOST);
        urlBuilder.setPath(BING_JSON_PATH);
        urlBuilder.setParameter(SearchParameters.APP_ID, this.appID);
        urlBuilder.setParameter(SearchParameters.QUERY, query);
        urlBuilder.setParameter(SearchParameters.SOURCES, source);
        urlBuilder.setParameter(SearchParameters.JSON_TYPE, JsonTypes.CALLBACK);
        
        for (Entry<String, String> additionalParam : additionalParams.entrySet())
        {
            urlBuilder.setParameter(additionalParam.getKey(), additionalParam.getValue());
        }
        return urlBuilder.buildString();
    }
}
