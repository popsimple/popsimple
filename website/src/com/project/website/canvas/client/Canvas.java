package com.project.website.canvas.client;

import java.util.logging.Level;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.RootPanel;
import com.project.shared.client.loggers.FirebugLogger;
import com.project.shared.client.loggers.GwtLogger;
import com.project.shared.client.loggers.HTMLLogger;
import com.project.shared.client.net.DynamicSourceLoader;
import com.project.shared.client.utils.UrlUtils;
import com.project.shared.utils.QueryString;
import com.project.shared.utils.ThrowableUtils;
import com.project.shared.utils.loggers.Logger;
import com.project.website.canvas.client.canvastools.base.BuiltinTools;
import com.project.website.canvas.client.canvastools.map.MapToolStaticUtils;
import com.project.website.canvas.client.resources.CanvasResources;
import com.project.website.shared.client.widgets.authentication.resources.AuthenticationResources;
import com.project.website.shared.data.QueryParameters;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Canvas implements EntryPoint {

    CanvasContainer canvasContainer = new CanvasContainer();

    public void onModuleLoad() {
        Logger.addLogger(GwtLogger.INSTANCE);
        Logger.addLogger(FirebugLogger.INSTANCE);
        Logger.addLogger(HTMLLogger.INSTANCE);

        try {
            performModuleLoad();
        }
        catch (Throwable e) {
            logUnhandledException(e);
        }
    }

    private void logUnhandledException(Throwable e) {
        Logger.log("Unhandled exception: " + e.getMessage() + " - " + ThrowableUtils.joinStackTrace(e), Level.SEVERE);
        if (null != e.getCause()) {
            Logger.log("Cause:", Level.SEVERE);
            logUnhandledException(e.getCause());
        }
    }

    private void performModuleLoad() {
        // start loading the maps, aloha apis immediately, in case it will be needed later.
        MapToolStaticUtils.loadApi();
        //AlohaEditor.loadApi();
        
        // Load Google +1 button script (asynchronously)
        DynamicSourceLoader.getLoadAsyncFunc("https://apis.google.com/js/plusone.js")
                           .run(null);


        // Make sure we have all resources loaded
        CanvasResources.INSTANCE.main().ensureInjected();
        AuthenticationResources.INSTANCE.main().ensureInjected();

        // Register the built-in canvas tools
        BuiltinTools.init();

        RootPanel.get("root").add(this.canvasContainer);

        History.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                QueryString queryString = QueryString.parse(UrlUtils.getUrlEncoder(), event.getValue());
            	String idStr = queryString.get(QueryParameters.PAGE_ID);
            	boolean viewMode = queryString.contains(QueryParameters.VIEW_MODE_FLAG);
            	String pageKey = queryString.get(QueryParameters.PAGE_KEY);
                canvasContainer.getWorksheet().load(idStr, viewMode, pageKey);
            }
        });

        // Go get'em!
        History.fireCurrentHistoryState();
    }
    
    
}
