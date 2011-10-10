package com.project.shared.data;

import com.project.shared.data.funcs.Func;
import com.project.shared.data.funcs.InvertibleFunc;

public abstract class StringEncoder extends InvertibleFunc<String, String>
{
    private final Func<String, String> _inverted = new Func<String, String>(){
        @Override public String apply(String arg) {
            return decode(arg);
        }};

    public abstract String encode(String value);
    public abstract String decode(String value);

    @Override
    public Func<String,String> invert()
    {
        return _inverted;
    }

    @Override
    public String apply(String arg)
    {
        return this.encode(arg);
    }

}
