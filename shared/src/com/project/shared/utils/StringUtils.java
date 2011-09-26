package com.project.shared.utils;


public class StringUtils
{
    public static String splitCamelCase(String camelCasedString, String seperator, boolean makelower)
    {
        StringBuilder dashedStringBuilder = new StringBuilder();
        dashedStringBuilder.append(camelCasedString.charAt(0));
        for (int i = 1; i < camelCasedString.length(); i++) {
            char c = camelCasedString.charAt(i);
            if (Character.isUpperCase(c)) {
                dashedStringBuilder.append(seperator);
                if (makelower) {
                    c = Character.toLowerCase(c);
                }
            }
            dashedStringBuilder.append(c);
        }
        return dashedStringBuilder.toString();
    }



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
