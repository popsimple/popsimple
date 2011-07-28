package com.project.gwtmapstraction.client.mxn;

public enum MapProvider {
    @Deprecated
    GOOGLE_V2("google", "Google Maps (old)"),

    GOOGLE_V3("googlev3", "Google Maps"),
    GOOGLE_EARTH("googleearth", "Google Earth"),
    YAHOO("yahoo", "Yahoo!"),
    MICROSOFT("microsoft", "Bing (Microsoft)"),
    MULTIMAP("multimap", "Multimap"),
    MAP24("map24", "Map24"),
    OPENLAYERS("openlayers", "OpenLayers", MapstractionMapType.ROAD),
    MAPQUEST("mapquest", "MapQuest");


    private final String apiString;
    private final String description;
    private final MapstractionMapType[] supportedMapTypes;


    private MapProvider(String apiString, String description)
    {
        this(apiString, description, MapstractionMapType.values());
    }

    private MapProvider(String apiString, String description, MapstractionMapType... supportedMapTypes)
    {
        this.apiString = apiString;
        this.description = description;
        this.supportedMapTypes = supportedMapTypes;
    }

    public String getApiString() {
        return this.apiString;
    }

    public String getDescription()
    {
        return description;
    }

    public MapstractionMapType[] getSupportedMapTypes()
    {
        return supportedMapTypes;
    }

}
