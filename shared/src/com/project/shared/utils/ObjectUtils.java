package com.project.shared.utils;

public class ObjectUtils
{
    /**
     * Compares two objects using 'equals', allowing for nulls
     */
    public static boolean areEqual(Object first, Object second)
    {
        return (null == first) ? (null == second) : first.equals(second);
    }
}
