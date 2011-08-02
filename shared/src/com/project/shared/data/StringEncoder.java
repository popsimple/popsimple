package com.project.shared.data;

import com.project.shared.data.funcs.InvertibleFunc;

public abstract class StringEncoder extends InvertibleFunc<String, String>
{
    public abstract String encode(String value);
    public abstract String decode(String value);

    @Override
    public String invertCall(String arg)
    {
        return this.decode(arg);
    }

    @Override
    public String call(String arg)
    {
        return this.encode(arg);
    }

}
