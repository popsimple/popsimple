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

    public static String join(String seperator, Iterable<String> strings)
    {
        StringBuilder builder = new StringBuilder();
        for (String str : strings) {
            builder.append(str);
            builder.append(seperator);
        }
        return builder.substring(0, builder.length() - seperator.length()).toString();
    }
}
