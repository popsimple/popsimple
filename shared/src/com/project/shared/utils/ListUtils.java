package com.project.shared.utils;

import java.util.ArrayList;
import java.util.Collection;

public class ListUtils
{
    public static <T> ArrayList<T> create(T... args) {
        ArrayList<T> res = new ArrayList<T>();
        for (T arg : args) {
            res.add(arg);
        }
        return res;
    }

    public static <T> ArrayList<T> exclude(Collection<T> iterable, T value)
    {
        ArrayList<T> res = new ArrayList<T>(iterable);
        res.remove(value);
        return res;
    }
}
