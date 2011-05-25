package com.project.gwtyoutube.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class YouTubeData extends JavaScriptObject {

    protected YouTubeData() {
    }

    public final native int getTotalItems() /*-{ return this.totalItems; }-*/;
    public final native int getStartIndex() /*-{ return this.startIndex; }-*/;
    public final native int getItemsPerPage() /*-{ return this.itemsPerPage; }-*/;
    
    public final native JsArray<YouTubeItem> getItems() /*-{
		return this.items;
    }-*/;
}
