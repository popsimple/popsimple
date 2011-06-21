package com.project.canvas.shared;

public class ObjectUtils
{
    public static boolean equals(Object first, Object second)
    {
        return first == null ? second == null : first.equals(second);
    }
}
