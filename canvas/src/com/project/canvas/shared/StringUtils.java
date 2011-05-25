package com.project.canvas.shared;

public class StringUtils 
{
    public static String DefaultIfEmptyOrNull(String str, String defaultStr)
    {
        return StringUtils.IsEmptyOrNull(str) ? defaultStr : str;
    }
    
    public static boolean IsEmptyOrNull(String str)
    {
        if ((null == str) || (str.isEmpty()))
        {
            return true;
        }
        return false;
    }
}
