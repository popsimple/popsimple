package com.project.gwtmapstraction.client.mxn;

import com.google.gwt.core.client.JavaScriptObject;

public class Marker extends JavaScriptObject {
    protected Marker() {}

    public static native final Marker create(LatLonPoint location)
    /*-{
        var c = $wnd.mxn.Marker;
        return new c(location);
    }-*/;
}
