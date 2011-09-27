package com.project.shared.utils;

import java.util.ArrayList;

import com.project.shared.data.funcs.Func;

public class IterableUtils
{
    public static <T, TRes> ArrayList<TRes> select(T[] elems, Func<T, TRes> func)
    {
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

    public static <T> ArrayList<T> reverse(Iterable<T> elems)
    {
        ArrayList<T> res = new ArrayList<T>();
        for (T elem : elems)
        {
            res.add(0, elem);
        }
        return res;
    }

    public static <U, T extends U> ArrayList<U> upCast(Iterable<T> elems)
    {
        ArrayList<U> res = new ArrayList<U>();
        for (T elem : elems)
        {
            res.add((U)elem);
        }
        return res;
    }

    public static <T> boolean contains(Iterable<T> values, T value)
    {
        for (T elem : values)
        {
            if ((elem == value) || ((null != elem) && (elem.equals(value))))
            {
                return true;
            }
        }
        return false;
    }

    public static <T> ArrayList<T> filter(T[] elems, Func<T, Boolean> func) {
        ArrayList<T> res = new ArrayList<T>();
        for (T elem : elems) {
            if (func.call(elem)) {
                res.add(elem);
            }
        }
        return res;
    }

    public static <T> ArrayList<T> toList(Iterable<T> elems)
    {
        ArrayList<T> res = new ArrayList<T>();
        for (T elem : elems) {
            res.add(elem);
        }
        return res;
    }

}
