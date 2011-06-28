package com.project.shared.utils;

public class ObjectUtils
{
    public static boolean equals(Object first, Object second)
    {
        return first == null ? second == null : first.equals(second);
    }
}
