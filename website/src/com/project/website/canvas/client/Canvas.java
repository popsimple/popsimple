package com.project.website.canvas.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.project.shared.client.loggers.FirebugLogger;
import com.project.shared.client.loggers.GwtLogger;
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

        CanvasResources.INSTANCE.main().ensureInjected();
        AuthenticationResources.INSTANCE.main().ensureInjected();

        BuiltinTools.init();
        RootPanel.get("root").add(this.canvasContainer);


        // start loading the maps api immediately, in case it will be needed later.
        MapToolStaticUtils.prepareApi();


        History.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                canvasContainer.getWorksheet().load(event.getValue());
            }
        });
        History.fireCurrentHistoryState();
    }
}
