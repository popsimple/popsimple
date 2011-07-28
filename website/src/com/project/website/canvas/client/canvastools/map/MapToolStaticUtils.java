package com.project.website.canvas.client.canvastools.map;

import java.util.ArrayList;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.project.gwtmapstraction.client.mxn.MapProvider;
import com.project.gwtmapstraction.client.mxn.MapstractionMapType;
import com.project.shared.client.events.SimpleEvent;
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
        MapProvider.GOOGLE_V3,
        MapProvider.MICROSOFT,
        MapProvider.OPENLAYERS
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

    protected static boolean loaded = false;

    public static void prepareApi() {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute()
            {
                MapToolStaticUtils.getLoadMapScriptsAsyncFunc().run(null);
            }
        });
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

    public static void loadMapScriptsAsync(Handler<Void> loadHandler) {
        getLoadMapScriptsAsyncFunc().then(HandlerUtils.toFunc(loadHandler))
                                    .run(null);
    }

    private static boolean isAvailable(MapProvider provider)
    {
        return AVAILABLE_PROVIDERS.contains(provider);
    }

    public static AsyncFunc<Void,Void> getLoadMapScriptsAsyncFunc()
    {
        AsyncFunc<Void, Void> res = AsyncFunc.immediate();
        for (MapProvider provider : AVAILABLE_PROVIDERS) {
            res = res.then(getLoadProviderAsyncFunc(provider));
        }
        return res.then(DynamicScriptLoader.getLoadAsyncFunc(MAPSTRACTION_SCRIPT_URL))
                  .then(new Func.VoidAction() {
                        @Override
                        public void exec()
                        {
                            MapToolStaticUtils.loaded = true;
                        }
                    });
    }

    public static boolean isApiLoaded() {
        return MapToolStaticUtils.loaded;
    }

    private static AsyncFunc<Void, Void> getLoadProviderAsyncFunc(MapProvider provider)
    {
        if (false == isAvailable(provider)) {
            throw new RuntimeException("Provider is not available: " + provider.name());
        }
        AsyncFunc<Void, Void> res = AsyncFunc.immediate();
        switch (provider) {
        case GOOGLE_V3:
            res = res.then(getLoadGoogleV3MapProviderAsyncFunc());
            break;
        case MICROSOFT:
            res = res.then(DynamicScriptLoader.getLoadAsyncFunc("http://ecn.dev.virtualearth.net/mapcontrol/mapcontrol.ashx?v=6.3"));
        case OPENLAYERS:
            res = res.then(DynamicScriptLoader.getLoadAsyncFunc("http://openlayers.org/api/OpenLayers.js"));
        default:
            break;
        }
        return res;
    }

    private static AsyncFunc<Void, Void> getLoadGoogleV3MapProviderAsyncFunc()
    {
        return new AsyncFunc<Void, Void>() {
            @Override
            protected <S, E> void run(Void arg, final Func<Void, S> successHandler, Func<Throwable, E> errorHandler)
            {
                loadGoogleV3MapProvider(new Handler<Void>(){
                    @Override
                    public void onFire(Void arg)
                    {
                        successHandler.call(null);
                    }
                });
            }
        };
    }

    // TODO: refactor this into DynamicScriptLoader
    // for scripts that have a callback
    private static void loadGoogleV3MapProvider(SimpleEvent.Handler<Void> handler)
    {
        if (googleV3MapProviderLoaded) {
            HandlerUtils.fireDeferred(handler, null);
            return;
        }
        // see http://code.google.com/apis/maps/documentation/javascript/basics.html#Async
        String source = "http://maps.googleapis.com/maps/api/js?sensor=false&callback=" + GoogleV3MapProviderCallbackName;
        defineGoogleV3MapProviderLoadedCallback();

        googleMapV3ScriptLoadedSuccessEvent.addHandler(handler);
        DynamicScriptLoader.getLoadAsyncFunc(source).run(null);
    }

    private static SimpleEvent<Void> googleMapV3ScriptLoadedSuccessEvent = new SimpleEvent<Void>();

    private static boolean googleV3MapProviderLoaded = false;
    private static final String GoogleV3MapProviderCallbackName = "GoogleV3MapProviderLoadedCallback";
    private static final native void defineGoogleV3MapProviderLoadedCallback() /*-{
        $wnd.GoogleV3MapProviderLoadedCallback = @com.project.website.canvas.client.canvastools.map.MapToolStaticUtils::GoogleMapV3ProviderLoaded();
    }-*/;

    static void GoogleMapV3ProviderLoaded()
    {
        googleV3MapProviderLoaded = true;
        SimpleEvent<Void> event = googleMapV3ScriptLoadedSuccessEvent;
        googleMapV3ScriptLoadedSuccessEvent = null;
        event.dispatch(null);
    }
}
