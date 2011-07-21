package com.project.shared.client.loggers;

import com.google.gwt.core.client.GWT;
import com.project.shared.utils.loggers.ILogger;


public class GwtLogger implements ILogger
{
    private GwtLogger() {}

    public static GwtLogger INSTANCE = new GwtLogger();

    @Override
    public void log(String str)
    {
        GWT.log(str);
    }

}
