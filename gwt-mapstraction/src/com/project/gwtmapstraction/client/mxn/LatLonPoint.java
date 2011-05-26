package com.project.gwtmapstraction.client.mxn;

import com.google.gwt.core.client.JavaScriptObject;

public class LatLonPoint extends JavaScriptObject {
    protected LatLonPoint() {}

    public static native final LatLonPoint create(double lat, double lon)
    /*-{
        var c = $wnd.mxn.LatLonPoint;
        return new c(lat, lon);
    }-*/;

}
