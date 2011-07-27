package com.project.shared.utils.loggers;

import java.util.ArrayList;

public class Logger
{

    private static ArrayList<ILogger> _loggers = new ArrayList<ILogger>();

    public static void addLogger(ILogger logger) {
        _loggers.add(logger);
    }

    public static void log(String str) {
        for (ILogger logger : _loggers) {
            logger.log(str);
        }
    }
}
