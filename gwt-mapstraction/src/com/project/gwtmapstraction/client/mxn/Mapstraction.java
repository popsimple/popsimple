package com.project.gwtmapstraction.client.mxn;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;

public class Mapstraction {
    private JavaScriptObject mapstraction;

    public Mapstraction(Element element, MapProvider api, boolean debug) {
        this.mapstraction = nativeConstructor(element, api.getApiString(),
                debug);
    }

    private final static native JavaScriptObject nativeConstructor(
            Element element, String apiString, boolean debug) /*-{
		$wnd.mxn.Mapstraction(element, apiString, debug);
    }-*/;

    /*
     * addControls(args) addControls adds controls to the map.
     *
     * addEventListener(type, func, caller) Add a listener for an event.
     *
     * addFilter(name, presently, the) addFilter adds a marker filter
     *
     * addImageOverlay(unique, url, opacity, west, south, east, north)
     * addImageOverlay layers an georeferenced image over the map
     *
     * addJSON(json)
     *
     * addLargeControls() Adds a large map panning control and zoom buttons to
     * the map
     *
     * addMapTypeControls() Adds a map type control to the map (streets, aerial
     * imagery etc)
     *
     * addMarker(marker, old) Adds a marker pin to the map
     *
     * addMarkerWithData(marker, data) addMarkerWithData will addData to the
     * marker, then add it to the map
     *
     * addOverlay(url, autoCenterAndZoom) Adds a GeoRSS or KML overlay to the
     * map some flavors of GeoRSS and KML are not supported by some of the Map
     * providers
     *
     * addPolyline(polyline, old) Add a polyline to the map
     *
     * addPolylineWithData(polyline, data) addPolylineWithData will addData to
     * the polyline, then add it to the map
     *
     * addSmallControls() Adds a small map panning control and zoom buttons to
     * the map
     *
     * addTileLayer(template, opacity, copyright, Minimum, Maximum, Should) Adds
     * a Tile Layer to the map Requires providing a parameterized tile url.
     *
     * applyFilter(o, f)
     *
     * applyOptions() Applies the current option settings
     *
     * autoCenterAndZoom() autoCenterAndZoom sets the center and zoom of the map
     * to the smallest bounding box containing all markers
     *
     * callEventListeners(sEventType, oEventArgs) Call listeners for a
     * particular event.
     *
     * centerAndZoomOnPoints(points) centerAndZoomOnPoints sets the center and
     * zoom of the map from an array of points This is useful if you don't want
     * to have to add markers to the map
     *
     * clickHandler(lat, lon, me)
     *
     * declutterMarkers(opts) Declutter the markers on the map, group together
     * overlapping markers.
     *
     * doFilter(showCallback, hideCallback) doFilter executes all filters added
     * since last call Now supports a callback function for when a marker is
     * shown or hidden
     *
     * dragging(on) Enable/disable dragging of the map
     *
     * enableScrollWheelZoom() Enable scroll wheel zooming
     *
     * getAttributeExtremes(name) getAttributeExtremes returns the
     * minimum/maximum of "field" from all markers
     *
     * getBounds() Gets the BoundingBox of the map
     *
     * getCenter() Gets the central point of the map
     *
     * getMap() getMap returns the native map object that mapstraction is
     * talking to
     *
     * getMapType() Gets the imagery type for the map.
     *
     * getPixelRatio() Returns a ratio to turn distance into pixels based on
     * current projection
     *
     * getZoom() Returns the zoom level of the map
     *
     * getZoomLevelForBoundingBox(bbox) Returns the best zoom level for bounds
     * given
     *
     */

    /**
     * Returns the loaded state of a Map Provider
     */
    private final native boolean isLoaded(String api)
    /*-{
        return this.mapstraction.isLoaded(api);
    }-*/;

    public boolean isLoaded(MapProvider api)
    {
        return this.isLoaded(api.getApiString());
    }

    public final native boolean isLoaded()
    /*-{
        return this.mapstraction.isLoaded();
    }-*/;

     /*
     * mousePosition(element) Displays the coordinates of the cursor in the HTML
     * element
     *
     * moveendHandler(me)
     *
     * polylineCenterAndZoom(radius) Automatically sets center and zoom level to
     * show all polylines Takes into account radious of polyline
     *
     * removeAllFilters() removeAllFilters
     *
     * removeAllMarkers() removeAllMarkers removes all the Markers on a map
     *
     * removeAllPolylines() Removes all polylines from the map
     *
     * removeFilter(field, operator, value) Remove the specified filter
     *
     * removeMarker(marker) removeMarker removes a Marker from the map
     *
     * removePolyline(polyline) Remove the polyline from the map
     *
     * resizeTo(width, height) Resize the current map to the specified width and
     * height (since it is actually on a child div of the mapElement passed as
     * argument to the Mapstraction constructor, the resizing of this mapElement
     * may have no effect on the size of the actual map)
     *
     * runDeferred() Run any queued provider API calls for the methods defined
     * in the provider's implementation.
     *
     * setBounds(bounds) Sets the map to the appropriate location and zoom for a
     * given BoundingBox
     *
     * setCenter(point, options) setCenter sets the central point of the map
     */

    public final native void setCenterAndZoom(LatLonPoint point, int zoom)
    /*-{
        this.mapstraction.setCenterAndZoom(point, zoom);
    }-*/;

     /* setCenterAndZoom(point, zoom) Centers the map to some place and zoom
     * level
     *
     * setDebug(debug) Set the debugging on or off - shows alert panels for
     * functions that don't exist in Mapstraction
     *
     * setDefer(set) Set the api call deferment on or off - When it's on,
     * mxn.invoke will queue up provider API calls until runDeferred is called,
     * at which time everything in the queue will be run in the order it was
     * added.
     *
     * setImageOpacity(id, opacity)
     *
     * setImagePosition(id)
     *
     * setMapType(type) Sets the imagery type for the map The type can be one
     * of: mxn.Mapstraction.ROAD mxn.Mapstraction.SATELLITE
     * mxn.Mapstraction.HYBRID mxn.Mapstraction.PHYSICAL
     *
     * setOption(sOptName, vVal) Sets an option and applies it.
     *
     * setOptions(oOpts) Sets the current options to those specified in oOpts
     * and applies them
     *
     * setZoom(zoom) Sets the zoom level for the map MS doesn't seem to do
     * zoom=0, and Gg's sat goes closer than it's maps, and MS's sat goes closer
     * than Y!'s TODO: Mapstraction.prototype.getZoomLevels or something.
     *
     * swap(api, element) Change the current api on the fly
     *
     * toggleFilter(field, operator, value) Delete the current filter if
     * present; otherwise add it
     *
     * toggleTileLayer(url) Turns a Tile Layer on or off
     *
     * visibleCenterAndZoom() Sets the center and zoom of the map to the
     * smallest bounding box containing all visible markers and polylines will
     * only include markers and polylines with an attribute of "visible"
     */
}
