package com.project.gwtmapstraction.client.mxn;

public enum MapProvider {
    @Deprecated
    GOOGLE_V2("google", "Google Maps (old)"),

    GOOGLE_V3("googlev3", "Google Maps", "http://maps.googleapis.com/maps/api/js?sensor=false"),
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
    private final String[] scriptsToLoad;

    public String[] getScriptsToLoad()
    {
        return scriptsToLoad;
    }

    private MapProvider(String apiString, String description) {
        this(apiString, description, new String[0]);
    }

    private MapProvider(String apiString, String description, String scriptToLoad) {
        this(apiString, description, new String[] { scriptToLoad });
    }

    private MapProvider(String apiString, String description, String[] scriptsToLoad) {
        this.apiString = apiString;
        this.description = description;
        this.scriptsToLoad = scriptsToLoad;
    }

    public String getApiString() {
        return this.apiString;
    }
}
