package com.project.website.canvas.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.RootPanel;
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
        CanvasResources.INSTANCE.main().ensureInjected();
        AuthenticationResources.INSTANCE.main().ensureInjected();

        // start loading the maps api immediately, in case it will be needed later.
        MapToolStaticUtils.prepareApi();

        BuiltinTools.init();

        RootPanel.get("root").add(this.canvasContainer);

        History.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                canvasContainer.getWorksheet().load(event.getValue());
            }
        });
        History.fireCurrentHistoryState();

    }
}
