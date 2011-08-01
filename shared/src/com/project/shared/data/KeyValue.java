package com.project.shared.data;

public class KeyValue<TKey, TValue> extends Pair<TKey, TValue>
{
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
