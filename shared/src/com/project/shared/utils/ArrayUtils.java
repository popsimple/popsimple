package com.project.shared.utils;

import java.util.ArrayList;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.project.shared.data.Pair;

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

    /**
     * Returns a list of pairs of items from the two arrays, with matching indices. 
     * The list's length is the minimum of the lengths of the two given arrays.
     */
    public static <T,U> ArrayList<Pair<T,U>> zip(T[] a, U[] b)
    {
    	int minLength = Math.min(a.length, b.length);
    	ArrayList<Pair<T,U>> result = new ArrayList<Pair<T,U>>();
    	for (int i = 0; i < minLength; i++) {
    		result.add(new Pair<T,U>(a[i], b[i]));
    	}
    	return result;
    }
}
