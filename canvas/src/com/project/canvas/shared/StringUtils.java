package com.project.canvas.shared;

public class StringUtils 
{
    public static String defaultIfEmptyOrNull(String str, String defaultStr)
    {
        return StringUtils.isEmptyOrNull(str) ? defaultStr : str;
    }
    
    public static boolean isEmptyOrNull(String str)
    {
        if ((null == str) || (str.isEmpty()))
        {
            return true;
        }
        return false;
    }
}
