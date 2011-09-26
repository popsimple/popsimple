package com.project.website.canvas.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.RootPanel;
import com.project.shared.client.loggers.FirebugLogger;
import com.project.shared.client.loggers.GwtLogger;
import com.project.shared.client.utils.UrlUtils;
import com.project.shared.utils.QueryString;
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

        // start loading the maps, aloha apis immediately, in case it will be needed later.
        MapToolStaticUtils.loadApi();
        //AlohaEditor.loadApi();

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
                canvasContainer.getWorksheet().load(idStr, viewMode);
            }
        });

        // Go get'em!
        History.fireCurrentHistoryState();
    }
}
