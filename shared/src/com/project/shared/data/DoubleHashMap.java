package com.project.shared.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DoubleHashMap<K1, K2> implements Map<K1, K2>
{
    private final HashMap<K1, K2> map1;
    private final HashMap<K2, K1> map2;

    private DoubleHashMap(HashMap<K1, K2> map1, HashMap<K2, K1> map2)
    {
        this.map1 = map1;
        this.map2 = map2;
    }
    
    public DoubleHashMap() {
        this.map1 = new HashMap<K1, K2>(); 
        this.map2 = new HashMap<K2, K1>();
    }

    public DoubleHashMap<K2, K1> reverseMap() {
        return new DoubleHashMap<K2, K1>(map2, map1);
    }

    public K2 getByKey1(K1 key) {
        return this.map1.get(key);
    }
    public K1 getByKey2(K2 key) {
        return this.map2.get(key);
    }

    @Override
    public int size()
    {
        return this.map1.size();
    }
    @Override
    public boolean isEmpty()
    {
        return this.map1.isEmpty();
    }
    @Override
    public boolean containsKey(Object key)
    {
        return this.map1.containsKey(key);
    }
    @Override
    public boolean containsValue(Object value)
    {
        return this.map1.containsValue(value);
    }
    @Override
    public K2 get(Object key)
    {
        return this.map1.get(key);
    }
    @Override
    public K2 put(K1 key, K2 value)
    {
        this.map2.put(value, key);
        return this.map1.put(key, value);
    }
    @Override
    public K2 remove(Object key)
    {
        K2 value = this.map1.remove(key);
        this.map2.remove(value);
        return value;
    }
    @Override
    public void putAll(Map<? extends K1, ? extends K2> m)
    {
        this.map1.putAll(m);
        for (Map.Entry<? extends K1, ? extends K2> entry : m.entrySet()) {
            this.map2.put(entry.getValue(), entry.getKey());
        }
    }
    @Override
    public void clear()
    {
        this.map1.clear();
        this.map2.clear();
    }
    @Override
    public Set<K1> keySet()
    {
        return map1.keySet();
    }
    @Override
    public Collection<K2> values()
    {
        return map1.values();
    }
    @Override
    public Set<java.util.Map.Entry<K1, K2>> entrySet()
    {
        return map1.entrySet();
    }
    
    
}
