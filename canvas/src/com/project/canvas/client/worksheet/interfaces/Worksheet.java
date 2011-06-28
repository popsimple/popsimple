package com.project.canvas.client.worksheet.interfaces;

import com.project.canvas.client.canvastools.base.ToolboxItem;
import com.project.shared.client.events.SimpleEvent;

public interface Worksheet {

    SimpleEvent<Boolean> getViewModeEvent();

    SimpleEvent<Void> getDefaultToolRequestEvent();

    void setActiveToolboxItem(ToolboxItem toolboxItem);

    void save();

    void load(String id);

}