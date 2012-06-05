package com.project.shared.client.loggers;

import java.util.logging.Level;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.utils.loggers.ILogger;

public class HTMLLogger implements ILogger 
{
    public static HTMLLogger INSTANCE = new HTMLLogger();

    private HTMLLogger() {}

    private final FlowPanel _panel = new FlowPanel();
    
    @Override
    public void log(String str, Level level) {
        Label label = new Label(level.toString() + " : " + str);
        label.addStyleName("logger-level-" + level.toString());
        _panel.add(label);
    }

    public Widget getLogWidget() {
        return this._panel;
    }
}
