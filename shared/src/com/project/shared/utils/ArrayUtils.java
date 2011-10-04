package com.project.shared.utils;

import java.util.ArrayList;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

public class ArrayUtils
{

    public static <T> ArrayList<T> filter(T[] elems, Function<T, Boolean> func) {
        ArrayList<T> res = new ArrayList<T>();
        for (T elem : elems) {
            if (func.apply(elem)) {
                res.add(elem);
            }
        }
        return res;
    }

    public static <T> Iterable<T> filter(T[] elems, Predicate<T> predicate)
    {
        return ArrayUtils.filter(elems, FunctionUtils.fromPredicate(predicate));
    }

}
