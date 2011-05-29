package com.project.gwtmapstraction.client.mxn;

public enum MapProvider {
    GOOGLE("google", "Google"),
    GOOGLE_V3("googlev3", "Google (alternate)"),
    YAHOO("yahoo", "Yahoo!"),
    MICROSOFT("microsoft", "Microsoft"),
    OPENSTREETMAP("openstreetmap", "OpenStreeMap"),
    MULTIMAP("multimap", "Multimap"),
    MAP24("map24", "Map24"),
    OPENLAYERS("openlayers", "OpenLayers"),
    MAPQUEST("mapquest", "MapQuest");

    public String getDescription()
    {
        return description;
    }

    private final String apiString;
    private final String description;

    private MapProvider(String apiString, String description) {
        this.apiString = apiString;
        this.description = description;
    }

    public String getApiString() {
        return this.apiString;
    }
}
