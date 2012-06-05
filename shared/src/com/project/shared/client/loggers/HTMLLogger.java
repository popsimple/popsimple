package com.project.shared.client.loggers;

import java.util.logging.Level;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.utils.loggers.ILogger;

public class HTMLLogger implements ILogger 
{
    public static HTMLLogger INSTANCE = new HTMLLogger();

    private HTMLLogger() {
        Style style = this._panel.getElement().getStyle();
        style.setPosition(Position.ABSOLUTE);
        style.setBottom(0, Unit.PX);
        style.setHeight(33, Unit.PCT);
        style.setLeft(0, Unit.PX);
        style.setRight(0, Unit.PX);
        style.setOverflow(Overflow.AUTO);
        style.setBackgroundColor("white");
        style.setZIndex(30000);
    }

    private final FlowPanel _panel = new FlowPanel();
    
    @Override
    public void log(String str, Level level) {
        String prefix = (null == level)
                      ? ""
                      : level.toString() + " : ";
        Label label = new Label(prefix + str);
        if (null != level) {
            label.addStyleName("logger-level-" + level.toString());
        }
        _panel.insert(label, 0);
    }

    public Widget getLogWidget() {
        return this._panel;
    }
}
