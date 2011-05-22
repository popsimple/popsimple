package com.project.canvas.client.canvastools.Image.BingImage.BingSearch;

import com.google.gwt.core.client.JavaScriptObject;

public class SearchResponse extends JavaScriptObject
{
    protected SearchResponse() 
    {
    }
    
    public final native String getVersion() /*-{ return this.Version; }-*/;
    
    public final native ImageResponse getImageResponse() /*-{ return this.Image; }-*/;
    
    //TODO: Define errors
}
