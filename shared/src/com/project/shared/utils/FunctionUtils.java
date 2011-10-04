package com.project.shared.utils;

import com.google.common.base.Predicate;
import com.project.shared.data.funcs.Func;

public class FunctionUtils
{

    public static <T> Func<T,Boolean> fromPredicate(final Predicate<T> predicate)
    {
        return new Func<T, Boolean>(){
            @Override public Boolean apply(T arg) {
                return predicate.apply(arg);
            }};
    }
}
