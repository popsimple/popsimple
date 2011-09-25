package com.project.shared.utils.loggers;

import java.util.ArrayList;
import java.util.logging.Level;

import com.google.gwt.core.client.GWT;

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

    public static void log(Object instance, String str, Level level) {
        str = (null == instance) ? str : instance.getClass().getName() + ": " + str;
        Logger.log(str, level);
    }

    public static void info(Object instance, String str) {
        Logger.log(instance, str, Level.INFO);
    }
}
