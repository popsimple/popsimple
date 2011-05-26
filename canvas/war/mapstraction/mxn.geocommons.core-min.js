/*
Copyright (c) 2011 Tom Carden, Steve Coast, Mikel Maron, Andrew Turner, Henri Bergius, Rob Moran, Derek Fowler
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of the Mapstraction nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
mxn.register("geocommons",{Mapstraction:{deferrable:{applyOptions:true,resizeTo:true,addControls:true,addSmallControls:true,addLargeControls:true,addMapTypeControls:true,dragging:true,setCenterAndZoom:true,getCenter:true,setCenter:true,setZoom:true,getZoom:true,getZoomLevelForBoundingBox:true,setMapType:true,getMapType:true,getBounds:true,setBounds:true,addTileLayer:true,toggleTileLayer:true,getPixelRatio:true,mousePosition:true},init:function(a,b){var c=this;this.element=a;this.loaded[this.api]=false;F1.Maker.core_host=f1_core_host;F1.Maker.finder_host=f1_finder_host;F1.Maker.maker_host=f1_maker_host;var d=new F1.Maker.Map({dom_id:this.element.id,flashvars:{},onload:function(f){c.maps[c.api]=f.swf;c.loaded[c.api]=true;for(var e=0;e<c.onload[c.api].length;e++){c.onload[c.api][e]()}}})},applyOptions:function(){var a=this.maps[this.api]},resizeTo:function(b,a){var c=this.maps[this.api];c.setSize(b,a)},addControls:function(a){var b=this.maps[this.api];b.showControl("Zoom",a.zoom||false);b.showControl("Layers",a.layers||false);b.showControl("Styles",a.styles||false);b.showControl("Basemap",a.map_type||false);b.showControl("Legend",a.legend||false,"open")},addSmallControls:function(){var a=this.maps[this.api];this.addControls({zoom:"small",legend:"open"})},addLargeControls:function(){var a=this.maps[this.api];this.addControls({zoom:"large",layers:true,legend:"open"})},addMapTypeControls:function(){var a=this.maps[this.api]},dragging:function(a){var b=this.maps[this.api]},setCenterAndZoom:function(a,b){var c=this.maps[this.api];c.setCenterZoom(a.lat,a.lon,b)},getCenter:function(){var b=this.maps[this.api];var a=b.getCenterZoom()[0];return new mxn.LatLonPoint(a.lat,a.lon)},setCenter:function(a,b){var c=this.maps[this.api];c.setCenter(a.lat,a.lon)},setZoom:function(a){var b=this.maps[this.api];b.setZoom(a)},getZoom:function(){var a=this.maps[this.api];return a.getZoom()},getZoomLevelForBoundingBox:function(e){var d=this.maps[this.api];var c=e.getNorthEast();var a=e.getSouthWest();var b;return b},setMapType:function(a){var b=this.maps[this.api];switch(a){case mxn.Mapstraction.ROAD:b.setMapProvider("OpenStreetMap (road)");break;case mxn.Mapstraction.SATELLITE:b.setMapProvider("BlueMarble");break;case mxn.Mapstraction.HYBRID:b.setMapProvider("Google Hybrid");break;default:b.setMapProvider(a)}},getMapType:function(){var a=this.maps[this.api];switch(a.getMapProvider()){case"OpenStreetMap (road)":return mxn.Mapstraction.ROAD;case"BlueMarble":return mxn.Mapstraction.SATELLITE;case"Google Hybrid":return mxn.Mapstraction.HYBRID;default:return null}},getBounds:function(){var b=this.maps[this.api];var a=b.getExtent();return new mxn.BoundingBox(a.northWest.lat,a.southEast.lon,a.southEast.lat,a.northWest.lon)},setBounds:function(b){var d=this.maps[this.api];var a=b.getSouthWest();var c=b.getNorthEast();d.setExtent(c.lat,a.lat,c.lon,a.lon)},addImageOverlay:function(c,a,e,i,f,g,d,h){var b=this.maps[this.api]},addOverlay:function(b,c){var d=this.maps[this.api];var a;if(typeof(b)==="number"){d.loadMap(b);return}a=b.match(/^(\d+)$/);if(a!==null){a=b.match(/^.*?maps\/(\d+)(\?\(\[?(.*?)\]?\))?$/)}d.loadMap(a[1])},addTileLayer:function(f,a,b,d,e){var c=this.maps[this.api]},toggleTileLayer:function(b){var a=this.maps[this.api]},getPixelRatio:function(){var a=this.maps[this.api]},mousePosition:function(a){var b=this.maps[this.api]},addMarker:function(b,a){var d=this.maps[this.api];var c=b.toProprietary(this.api);return c},removeMarker:function(a){var b=this.maps[this.api]},declutterMarkers:function(a){var b=this.maps[this.api]},addPolyline:function(b,a){var d=this.maps[this.api];var c=b.toProprietary(this.api);return c},removePolyline:function(a){var b=this.maps[this.api]}},LatLonPoint:{toProprietary:function(){return{}},fromProprietary:function(a){}},Marker:{toProprietary:function(){return{}},openBubble:function(){},hide:function(){},show:function(){},update:function(){}},Polyline:{toProprietary:function(){return{}},show:function(){},hide:function(){}}});