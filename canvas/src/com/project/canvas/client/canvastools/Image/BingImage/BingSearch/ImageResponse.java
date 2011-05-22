package com.project.canvas.client.canvastools.Image.BingImage.BingSearch;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class ImageResponse extends JavaScriptObject 
{
    protected ImageResponse()
    {
    }
    
    public final native int getTotal() /*-{ return this.Total; }-*/;
    
    public final native int getOffset() /*-{ return this.Offset; }-*/;
    
    public final native JsArray<ImageResult> getResults() /*-{ return this.Results; }-*/;
}
