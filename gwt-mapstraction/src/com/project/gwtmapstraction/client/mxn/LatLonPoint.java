package com.project.gwtmapstraction.client.mxn;

import com.google.gwt.core.client.JavaScriptObject;

public class LatLonPoint extends JavaScriptObject {
    protected LatLonPoint() {}

    public static native final LatLonPoint create(double lat, double lon)
    /*-{
        var c = $wnd.mxn.LatLonPoint;
        return new c(lat, lon);
    }-*/;

    /** Returns latitude conversion based on current projection  */
    public final native double latConv() /*-{
        return this.latConv();
    }-*/;

    /** Returns longitude conversion based on current projection  */
    public final native double lonConv() /*-{
        return this.lonConv();
    }-*/;

    public final native double getLat() /*-{
        return this.lat;
    }-*/;

    public final native double getLon() /*-{
        return this.lon;
    }-*/;
}
