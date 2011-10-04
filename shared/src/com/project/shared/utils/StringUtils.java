package com.project.shared.utils;


import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;



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



    public static String defaultIfNullOrEmpty(String str, String defaultStr)
    {
        return Strings.isNullOrEmpty(str) ? defaultStr : str;
    }

    public static boolean isWhitespaceOrNull(String str)
    {
        return CharMatcher.WHITESPACE.trimFrom(Strings.nullToEmpty(str)).isEmpty();
    }

}
