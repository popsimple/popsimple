package com.project.website.canvas.client.canvastools.map;

import java.util.ArrayList;

import com.project.gwtmapstraction.client.mxn.MapProvider;
import com.project.gwtmapstraction.client.mxn.MapstractionMapType;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.client.utils.DynamicScriptLoader;
import com.project.shared.client.utils.HandlerUtils;
import com.project.shared.data.funcs.AsyncFunc;
import com.project.shared.data.funcs.Func;
import com.project.shared.utils.IterableUtils;
import com.project.shared.utils.ListUtils;
import com.project.shared.utils.StringUtils;
import com.project.website.canvas.shared.data.MapData.MapType;

public class MapToolStaticUtils
{
    public static final ArrayList<MapProvider> AVAILABLE_PROVIDERS = ListUtils.create(
        MapProvider.GOOGLE_V3
//        MapProvider.MICROSOFT,
//        MapProvider.OPENSTREETMAP
    );

    private static final ArrayList<String> MAPSTRACTION_AVAILABLE_API_STRINGS = IterableUtils.select(AVAILABLE_PROVIDERS, new Func<MapProvider, String>(){
        @Override
        public String call(MapProvider arg)
        {
            return arg.getApiString();
        }
    });
    private static final String MAPSTRACTION_AVAILABLE_APIS = StringUtils.join(",", MAPSTRACTION_AVAILABLE_API_STRINGS);
    private static final String MAPSTRACTION_SCRIPT_FILE_URL = "mapstraction/mxn.js";
    private static final String MAPSTRACTION_SCRIPT_URL
        = MAPSTRACTION_SCRIPT_FILE_URL + "?(" + MAPSTRACTION_AVAILABLE_APIS + ")";

    public static void prepareApi() {
        MapToolStaticUtils.actionLoadMapScripts().run(null);
    }

    public static MapType fromMapstractionMapType(MapstractionMapType mapstractionMapType)
    {
        switch (mapstractionMapType) {
            case HYBRID:
                return MapType.HYBRID;
            case PHYSICAL:
                return MapType.PHYSICAL;
            case ROAD:
                return MapType.ROAD;
            case SATELLITE:
                return MapType.SATELLITE;
            default:
                throw new RuntimeException("Unsupported mapstraction map type: " + mapstractionMapType);
        }
    }

    public static MapstractionMapType fromMapType(MapType mapType)
    {
        switch (mapType) {
            case HYBRID:
                return MapstractionMapType.HYBRID;
            case PHYSICAL:
                return MapstractionMapType.PHYSICAL;
            case ROAD:
                return MapstractionMapType.ROAD;
            case SATELLITE:
                return MapstractionMapType.SATELLITE;
            default:
                throw new RuntimeException("Unsupported map type: " + mapType);
        }
    }

    public static void loadMapScripts(Handler<Void> loadHandler) {
        actionLoadMapScripts().then(HandlerUtils.toFunc(loadHandler))
                              .run(null);
    }

    public static AsyncFunc<Void,Void> actionLoadMapScripts()
    {
        AsyncFunc<Void, Void> res = AsyncFunc.immediate();
        for (MapProvider provider : AVAILABLE_PROVIDERS) {
            res = res.then(actionLoadProvider(provider));
        }
        return res.then(DynamicScriptLoader.actionLoad(MAPSTRACTION_SCRIPT_URL));
    }

    private static AsyncFunc<Void, Void> actionLoadProvider(MapProvider provider)
    {
        if (false == isAvailable(provider)) {
            throw new RuntimeException("Provider is not available: " + provider.name());
        }
        AsyncFunc<Void, Void> res = AsyncFunc.immediate();
        for (String source : provider.getScriptsToLoad()) {
            res = res.then(DynamicScriptLoader.actionLoad(source));
        }
        return res;
    }

    private static boolean isAvailable(MapProvider provider)
    {
        return AVAILABLE_PROVIDERS.contains(provider);
    }

}
