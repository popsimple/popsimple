package com.project.canvas.client.shared.searchProviders.youtube;

import com.google.gwt.core.client.JavaScriptObject;

public class YouTubeThumbnail extends JavaScriptObject 
{
    protected YouTubeThumbnail()
    {
    }
    
    public final native String getDefaultUrl() /*-{ return this['default']; }-*/;
    public final native String getSQDefaultUrl() /*-{ return this.sqDefault; }-*/;
    public final native String getHQDefaultUrl() /*-{ return this.hqDefault; }-*/;
}
