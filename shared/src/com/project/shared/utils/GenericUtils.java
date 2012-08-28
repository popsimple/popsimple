package com.project.shared.utils;

public class GenericUtils {
    public static <T> T defaultIfNull(T obj, T _default) {
        return obj != null ? obj : _default;
    }
    
    public static <T> boolean areEqual(T obj1, T obj2)
    {
        return (obj1 != null) && obj1.equals(obj2);
    }
    
    public static <T> String safeToString(T obj)
    {
        return GenericUtils.safeToString(obj, "<null>");
    }
    
    public static <T> String safeToString(T obj, String nullString)
    {
        if (null == obj)
        {
            return nullString;
        }
        return obj.toString();
    }
}
