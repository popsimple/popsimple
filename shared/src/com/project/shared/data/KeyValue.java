package com.project.shared.data;

public class KeyValue<TKey, TValue> extends Pair<TKey, TValue>
{
    private static final long serialVersionUID = 1L;

    public KeyValue(TKey key, TValue value)
    {
        super(key, value);
    }

    public TKey getKey()
    {
        return this.a;
    }

    public TValue getValue()
    {
        return this.b;
    }
}
