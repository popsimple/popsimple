package com.project.canvas.client.shared.searchProviders.youtube;

import com.google.gwt.core.client.JavaScriptObject;

public class YouTubeItem extends JavaScriptObject 
{
    protected YouTubeItem()
    {
    }
    
    public final native String getId() /*-{ return this.id; }-*/;
    public final native String getTitle() /*-{ return this.title; }-*/;
    
    public final native YouTubeThumbnail getThumbnail() /*-{ return this.thumbnail; }-*/;
}
