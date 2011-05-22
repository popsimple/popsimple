package com.project.canvas.client.canvastools.Image.BingImage.BingSearch;


public class ImageResult extends ImageBase 
{
    protected ImageResult()
    {
    }
    
    public final native String getTitle() /*-{ return this.Title; }-*/;
    public final native String getMediaUrl() /*-{ return this.MediaUrl; }-*/;
    public final native String getDisplayUrl() /*-{ return this.DisplayUrl; }-*/;
    public final native Thumbnail getThumbnail() /*-{ return this.Thumbnail; }-*/;
}
