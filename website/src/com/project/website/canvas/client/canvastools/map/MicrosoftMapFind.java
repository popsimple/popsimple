package com.project.website.canvas.client.canvastools.map;

import com.google.gwt.core.client.JavaScriptObject;


public abstract class MicrosoftMapFind
{
    public abstract void callback(boolean found, double lat, double lon, int zoomLevel);


    public native void find(JavaScriptObject map, String text) /*-{
        var me = this;
        //var _vemap = $wnd.VEMap;
        //var map = new _vemap(elemId);
        map.LoadMap();
        var callback = function(layer, resultsArray, places, hasMore, veErrorMessage) {
            var lat = 0;
            var lon = 0;
            var found = false;
            var zoomLevel = 1;
            if ((null != places) && (places.length > 0)) {
                lat = places[0].LatLong.Latitude;
                lon = places[0].LatLong.Longitude;
                zoomLevel = map.GetZoomLevel();
                found = true;
            }
            me.@com.project.website.canvas.client.canvastools.map.MicrosoftMapFind::callback(ZDDI)(found, lat, lon, zoomLevel);
        };

        map.Find(null, text, null, null, 0, 10, true, true, true, true, callback);
    }-*/;
}
