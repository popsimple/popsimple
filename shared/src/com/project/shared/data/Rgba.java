package com.project.shared.data;

public class Rgba
{
    private final int r;
    private final int g;
    private final int b;
    private final int a;

    public int getR()
    {
        return r;
    }

    public int getG()
    {
        return g;
    }

    public int getB()
    {
        return b;
    }

    public int getA()
    {
        return a;
    }

    public Rgba(int r, int g, int b, int a)
    {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Rgba(int r, int g, int b)
    {
        this(r,g,b,0);
    }


}
