package com.project.gwtmapstraction.client.mxn;

public enum MapProvider {
    GOOGLE("google"),
    GOOGLE_V3("googlev3"),
    YAHOO("yahoo"),
    MICROSOFT("microsoft"),
    OPENSTREETMAP("openstreetmap"),
    MULTIMAP("multimap"),
    MAP24("map24"),
    OPENLAYERS("openlayers"),
    MAPQUEST("mapquest");

    private final String apiString;

    private MapProvider(String apiString) {
        this.apiString = apiString;
    }

    public String getApiString() {
        return this.apiString;
    }
}
