package com.project.shared.utils;

import java.util.ArrayList;

public class IterableUtils
{
    public interface Func<T, TRes> {
        TRes execute(T arg);
    }

    public static <T, TRes> ArrayList<TRes> select(T[] elems, Func<T, TRes> func) {
        ArrayList<TRes> res = new ArrayList<TRes>();
        for (T elem : elems) {
            res.add(func.execute(elem));
        }
        return res;
    }
    public static <T, TRes> ArrayList<TRes> select(Iterable<T> elems, Func<T, TRes> func) {
        ArrayList<TRes> res = new ArrayList<TRes>();
        for (T elem : elems) {
            res.add(func.execute(elem));
        }
        return res;
    }
}
