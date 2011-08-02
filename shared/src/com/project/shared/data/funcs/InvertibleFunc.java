package com.project.shared.data.funcs;

/**
 * An invertible function (bijection)
 */
public abstract class InvertibleFunc<A,B> extends Func<A,B>
{
    public abstract A invertCall(B arg);
}
