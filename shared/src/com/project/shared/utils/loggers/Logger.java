package com.project.shared.utils.loggers;

import java.util.ArrayList;
import java.util.logging.Level;

import com.google.gwt.core.client.GWT;
import com.project.shared.utils.ThrowableUtils;

public class Logger
{

    private static ArrayList<ILogger> _loggers = new ArrayList<ILogger>();

    public static void addLogger(ILogger logger) {
        _loggers.add(logger);
    }

    public static void info(String str) {
        Logger.log(str, Level.INFO);
    }

    public static void log(String str, Level level) {
        if (GWT.isProdMode()) {
            return;
        }
        for (ILogger logger : _loggers) {
            logger.log(str, level);
        }
    }

    public static void log(Class<?> cls, String str, Level level) {
        str = (null == cls) ? str : cls.getName() + ": " + str;
        Logger.log(str, level);
    }

    public static void info(Class<?> cls, String str) {
        Logger.log(cls, str, Level.INFO);
    }

    public static void info(Object instance, String str) {
        Logger.log(null == instance ? null : instance.getClass(), str, Level.INFO);
    }

    public static void printStack()
    {
        try {
            throw new RuntimeException();
        }
        catch (Throwable e) {
            Logger.info(ThrowableUtils.joinStackTrace(e));
        }
    }
}
