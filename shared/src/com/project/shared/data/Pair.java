package com.project.shared.data;

import java.io.Serializable;


public class Pair<A, B> implements Serializable
{
    private static final long serialVersionUID = 1L;

    protected final A a;
    protected final B b;

    public Pair(A a, B b)
    {
        this.a = a;
        this.b = b;
    }

    public A getA()
    {
        return a;
    }

    public B getB()
    {
        return b;
    }


}
