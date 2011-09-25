package com.project.website.canvas.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.RootPanel;
import com.project.shared.client.loggers.FirebugLogger;
import com.project.shared.client.loggers.GwtLogger;
import com.project.shared.utils.ObjectUtils;
import com.project.shared.utils.loggers.Logger;
import com.project.website.canvas.client.canvastools.base.BuiltinTools;
import com.project.website.canvas.client.canvastools.map.MapToolStaticUtils;
import com.project.website.canvas.client.resources.CanvasResources;
import com.project.website.shared.client.widgets.authentication.resources.AuthenticationResources;

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
            	String[] parts = event.getValue().toLowerCase().trim().split(":");
            	String idStr = parts[0];
            	boolean viewMode = false;
            	if ((parts.length > 1) && (ObjectUtils.areEqual(parts[1], "view"))) {
            		viewMode = true;
            	}
                canvasContainer.getWorksheet().load(idStr, viewMode);
            }
        });

        // Go get'em!
        History.fireCurrentHistoryState();
    }
}
