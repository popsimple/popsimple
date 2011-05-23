package com.project.gwtbing.client;

import com.google.gwt.core.client.JavaScriptObject;

public class SearchResult extends JavaScriptObject 
{
    protected SearchResult()
    {
    }
    
    public final native SearchResponse getSearchResponse() /*-{ return this.SearchResponse; }-*/;
}
