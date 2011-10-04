package com.project.shared.utils;

import java.util.ArrayList;


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
}
