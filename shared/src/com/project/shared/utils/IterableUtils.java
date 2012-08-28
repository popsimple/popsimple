package com.project.shared.utils;

import java.util.ArrayList;
import java.util.Iterator;

import com.project.shared.data.Pair;


public class IterableUtils
{
    public static <U, T extends U> ArrayList<U> upCast(Iterable<T> elems)
    {
        ArrayList<U> res = new ArrayList<U>();
        for (T elem : elems)
        {
            res.add((U)elem);
        }
        return res;
    }
    
    /**
     * Returns a list of pairs of items in matching order (e.g. (a1, b1), (a2, b2), ...) from the two given iterables.
     * The list will be filled until the first iterable stop iterating.
     */
    public static <T,U> ArrayList<Pair<T,U>> zip(Iterable<T> a, Iterable<U> b)
    {
    	ArrayList<Pair<T,U>> result = new ArrayList<Pair<T,U>>();
    	Iterator<T> aTerator = a.iterator();
    	Iterator<U> bTerator = b.iterator();
    	while (aTerator.hasNext() && bTerator.hasNext()) {
    		result.add(new Pair<T,U>(aTerator.next(), bTerator.next()));
    	}
    	return result;
    }

    public static <T> ArrayList<T> toArrayList(Iterable<T> elems)
    {
        ArrayList<T> res = new ArrayList<T>();
        for (T elem : elems)
        {
            res.add(elem);
        }
        return res;
    }
}
