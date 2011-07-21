package com.project.shared.utils;

import java.util.ArrayList;

import com.project.shared.data.funcs.Func;

public class IterableUtils
{
      public static <T, TRes> ArrayList<TRes> select(T[] elems, Func<T, TRes> func) {
        ArrayList<TRes> res = new ArrayList<TRes>();
        for (T elem : elems) {
            res.add(func.call(elem));
        }
        return res;
    }
    public static <T, TRes> ArrayList<TRes> select(Iterable<T> elems, Func<T, TRes> func) {
        ArrayList<TRes> res = new ArrayList<TRes>();
        for (T elem : elems) {
            res.add(func.call(elem));
        }
        return res;
    }
}
