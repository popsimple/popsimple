package com.project.website.canvas.client.worksheet.interfaces;

import com.project.shared.client.events.SimpleEvent;
import com.project.website.canvas.client.canvastools.base.ToolboxItem;

public interface Worksheet {

    SimpleEvent<Boolean> getViewModeEvent();

    SimpleEvent<Void> getDefaultToolRequestEvent();

    void setActiveToolboxItem(ToolboxItem toolboxItem);

    void save();

    void load(String id);

}