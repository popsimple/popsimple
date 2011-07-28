package com.project.gwtmapstraction.client.mxn;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;

public class Mapstraction extends JavaScriptObject {
	protected Mapstraction() {}

	public static Mapstraction createInstance(Element element, MapProvider provider, boolean debug)
	{
		return Mapstraction.createInstance(element, provider.getApiString(), debug);
	}

	private final static native Mapstraction createInstance(Element element, String apiString, boolean debug)
	/*-{
		if ((null == $wnd.mxn) || (null == $wnd.mxn.Mapstraction)) {
			throw 'Mapstraction is not loaded properly';
		}
        var c = $wnd.mxn.Mapstraction;
		return new c(element, apiString, debug);
    }-*/;

    public final native Element getCurrentElement()
    /*-{
        return this.currentElement;
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
     * addMapTypeControls()
     */

	/**
	 * Adds a map type control to the map (streets, aerial
     * imagery etc)
	 */
	public final native void addMapTypeControls() /*-{
		this.addMapTypeControls();
	}-*/;

     /* addMarker(marker, old) Adds a marker pin to the map
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
     */

	public final native void addSmallControls() /*-{
        this.addSmallControls();
    }-*/;

     /*
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
     */

     /** Enable scroll wheel zooming
      */
	public native final void enableScrollWheelZoom() /*-{
	    this.enableScrollWheelZoom();
	}-*/;

     /*
     * getAttributeExtremes(name) getAttributeExtremes returns the
     * minimum/maximum of "field" from all markers
     *
     * getBounds() Gets the BoundingBox of the map
     */

	/** Gets the central point of the map
     */
	public native final LatLonPoint getCenter() /*-{
	    return this.getCenter();
	}-*/;

     /* getMap() getMap returns the native map object that mapstraction is
     * talking to
     */
     /* getMapType() Gets the imagery type for the map.
     */
	private final native int getMapTypeInternal() /*-{
	    return this.getMapType();
	}-*/;

	public final MapstractionMapType getMapType() {
	    int mapType = this.getMapTypeInternal();
        if (this.mapTypeHybrid() == mapType) {
            return MapstractionMapType.HYBRID;
        }
        if (this.mapTypePhysical() == mapType) {
            return MapstractionMapType.PHYSICAL;
        }
        if (this.mapTypeRoad() == mapType) {
            return MapstractionMapType.ROAD;
        }
        if (this.mapTypeSatellite() == mapType) {
            return MapstractionMapType.SATELLITE;
        }
        throw new RuntimeException("Unsupported map type stored in internal map: " + mapType);
	}

     /* getPixelRatio() Returns a ratio to turn distance into pixels based on
     * current projection
     */

     /** Returns the zoom level of the map
     */
	public final native int  getZoom() /*-{
	    return this.getZoom();
	}-*/;

     /* getZoomLevelForBoundingBox(bbox) Returns the best zoom level for bounds
     * given
     *
     */

    /**
     * Returns the loaded state of a Map Provider
     */
    public final native boolean isLoaded(String api)
    /*-{
        return this.isLoaded(api);
    }-*/;

    public final native boolean isLoaded()
    /*-{
        return this.isLoaded();
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
     */
    public final native void resizeTo(int width, int height) /*-{
        this.resizeTo(width, height);
    }-*/;
     /* runDeferred() Run any queued provider API calls for the methods defined
     * in the provider's implementation.
     *
     * setBounds(bounds) Sets the map to the appropriate location and zoom for a
     * given BoundingBox
     */

     /** sets the central point of the map
     */
    public final native void setCenter(LatLonPoint point) /*-{
        this.setCenter(point);
    }-*/;
    //public final native void setCenter(LatLonPoint point, options)


    public final native void setCenterAndZoom(LatLonPoint point, int zoom)
    /*-{
        this.setCenterAndZoom(point, zoom);
    }-*/;

     /* setCenterAndZoom(point, zoom) Centers the map to some place and zoom
     * level
     */
     /** Set the debugging on or off - shows alert panels for
     * functions that don't exist in Mapstraction
     */
    public final native void setDebug(boolean debug) /*-{
        this.setDebug(debug);
    }-*/;


     /* setDefer(set) Set the api call deferment on or off - When it's on,
     * mxn.invoke will queue up provider API calls until runDeferred is called,
     * at which time everything in the queue will be run in the order it was
     * added.
     *
     * setImageOpacity(id, opacity)
     *
     * setImagePosition(id)
     *
     */

    /* setMapType(type) Sets the imagery type for the map The type can be one
     * of: mxn.Mapstraction.ROAD mxn.Mapstraction.SATELLITE
     * mxn.Mapstraction.HYBRID mxn.Mapstraction.PHYSICAL
     */
    private final native void setMapType(int  mapType) /*-{
        this.setMapType(mapType);
    }-*/;

    // TODO: find a better way to tie the enum values to the javascript constants
    private final native int mapTypeRoad() /*-{
        return $wnd.mxn.Mapstraction.ROAD;
    }-*/;
    private final native int mapTypePhysical() /*-{
        return $wnd.mxn.Mapstraction.PHYSICAL;
    }-*/;
    private final native int mapTypeSatellite() /*-{
        return $wnd.mxn.Mapstraction.SATELLITE;
    }-*/;
    private final native int mapTypeHybrid() /*-{
        return $wnd.mxn.Mapstraction.HYBRID;
    }-*/;

    public final void setMapType(MapstractionMapType mapType) {
        int  jsMapType;
        switch (mapType)
        {
            case ROAD: jsMapType = this.mapTypeRoad(); break;
            case HYBRID: jsMapType = this.mapTypeHybrid(); break;
            case SATELLITE: jsMapType = this.mapTypeSatellite(); break;
            case PHYSICAL: jsMapType = this.mapTypePhysical(); break;
            default:
                throw new RuntimeException("Unsupported map type: " + mapType);
        }
        this.setMapType(jsMapType);
    }

     /* setOption(sOptName, vVal) Sets an option and applies it.
     *
     * setOptions(oOpts) Sets the current options to those specified in oOpts
     * and applies them
     */

     /** Sets the zoom level for the map. MS doesn't seem to do
     * zoom=0, and Gg's sat goes closer than it's maps, and MS's sat goes closer
     * than Y!'s TODO: Mapstraction.prototype.getZoomLevels or something.
     */
    public final native void setZoom(int zoom) /*-{
        this.setZoom(zoom);
    }-*/;


    private final native void swap(String element, String api) /*-{
        this.swap(element, api);
    }-*/;

    /** Change the current api on the fly
     */
    public final void swap(MapProvider provider, Element element) {
        this.swap(element.getId(), provider.getApiString());
    }

     /* toggleFilter(field, operator, value) Delete the current filter if
     * present; otherwise add it
     *
     * toggleTileLayer(url) Turns a Tile Layer on or off
     *
     * visibleCenterAndZoom() Sets the center and zoom of the map to the
     * smallest bounding box containing all visible markers and polylines will
     * only include markers and polylines with an attribute of "visible"
     */
}
