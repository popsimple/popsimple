package com.project.shared.utils;

public class StringUtils
{
    public static String defaultIfEmptyOrNull(String str, String defaultStr)
    {
        return StringUtils.isEmptyOrNull(str) ? defaultStr : str;
    }

    public static boolean isEmptyOrNull(String str)
    {
        return (str == null) || str.isEmpty();
    }

    public static boolean isWhitespaceOrNull(String str)
    {
        return (str == null) || str.trim().isEmpty();
    }
}
