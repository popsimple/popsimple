package com.project.shared.utils;

import java.util.ArrayList;

public class ListUtils
{
    public static <T> ArrayList<T> create(T... args) {
        ArrayList<T> res = new ArrayList<T>();
        for (T arg : args) {
            res.add(arg);
        }
        return res;
    }
}
