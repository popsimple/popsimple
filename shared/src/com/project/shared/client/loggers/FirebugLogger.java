package com.project.shared.client.loggers;

import com.project.shared.utils.loggers.ILogger;


public class FirebugLogger implements ILogger
{
    public static FirebugLogger INSTANCE = new FirebugLogger();

    private FirebugLogger() {}

    @Override
    public void log(String str)
    {
        this.firebugLog(str);
    }

    private final native void firebugLog(String str) /*-{
        $wnd.console.log(str);
    }-*/;
}
