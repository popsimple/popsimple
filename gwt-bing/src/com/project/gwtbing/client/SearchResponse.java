package com.project.gwtbing.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.project.gwtbing.client.imagesearch.ImageResponse;

public class SearchResponse extends JavaScriptObject
{
    protected SearchResponse() 
    {
    }
    
    public final native String getVersion() /*-{ return this.Version; }-*/;

    public final native ImageResponse getImageResponse() /*-{ return this.Image; }-*/;
    //TODO: Define errors
}
