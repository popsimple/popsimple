package com.project.website.canvas.client.worksheet.interfaces;

import com.google.gwt.event.shared.HandlerRegistration;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.data.funcs.AsyncFunc;
import com.project.website.canvas.client.canvastools.base.interfaces.ToolboxItem;
import com.project.website.canvas.shared.data.CanvasPage;

public interface Worksheet {

    HandlerRegistration addDefaultToolboxItemRequestHandler(SimpleEvent.Handler<Void> handler);

    HandlerRegistration addViewModeChangedHandler(SimpleEvent.Handler<Boolean> handler);

    void setActiveToolboxItem(ToolboxItem toolboxItem);

    void save();

    void load(String id, String pageKey);

	void load(String idStr, boolean viewMode, String pageKey);


}