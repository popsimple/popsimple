package com.project.gwtyoutube.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class YouTubeData extends JavaScriptObject {

    protected YouTubeData() {
    }

    public final native JsArray<YouTubeItem> getItems() /*-{
		return this.items;
    }-*/;
}
