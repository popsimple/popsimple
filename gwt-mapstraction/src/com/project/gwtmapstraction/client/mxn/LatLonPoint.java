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
        // Never return nulls - java return type is double
        // see http://helephant.com/2008/12/09/javascript-null-or-default-operator/
        return this.latConv() || 0.0;
    }-*/;

    /** Returns longitude conversion based on current projection  */
    public final native double lonConv() /*-{
        // Never return nulls - java return type is double
        return this.lonConv() || 0.0;
    }-*/;

    public final native double getLat() /*-{
        // Never return nulls - java return type is double
        return this.lat || 0.0;
    }-*/;

    public final native double getLon() /*-{
        // Never return nulls - java return type is double
        return this.lon || 0.0;
    }-*/;
}
