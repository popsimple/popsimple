package com.project.shared.client.loggers;

import java.util.logging.Level;

import com.project.shared.utils.loggers.ILogger;


public class FirebugLogger implements ILogger
{
    public static FirebugLogger INSTANCE = new FirebugLogger();

    private FirebugLogger() {}

    /**
     * Ignores the Level attribute, uses firebug's console.log for all levels.
     */
    @Override
    public void log(String str, Level level)
    {
        this.firebugLog(str);
    }


    private final native void firebugLog(String str) /*-{
        if ($wnd.console != undefined) {
            $wnd.console.log(str);
        }
    }-*/;
}
