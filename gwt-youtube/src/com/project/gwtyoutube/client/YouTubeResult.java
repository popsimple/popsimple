package com.project.gwtyoutube.client;

import com.google.gwt.core.client.JavaScriptObject;

public class YouTubeResult extends JavaScriptObject
{
    protected YouTubeResult()
    {
    }
    
    public final native String getApiVersion() /*-{ return this.apiVersion; }-*/;
    public final native YouTubeData getData() /*-{ return this.data; }-*/;
}
