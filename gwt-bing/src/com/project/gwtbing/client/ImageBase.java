package com.project.gwtbing.client;

import com.google.gwt.core.client.JavaScriptObject;

public class ImageBase extends JavaScriptObject 
{
    protected ImageBase()
    {
    }
    
    public final native int getFileSize() /*-{ return this.FileSize; }-*/;
    public final native String getUrl() /*-{ return this.Url; }-*/;
    public final native int getWidth() /*-{ return this.Width; }-*/;
    public final native int getHeight() /*-{ return this.Height; }-*/;
    public final native String getContentType() /*-{ return this.ContentType; }-*/;
}
