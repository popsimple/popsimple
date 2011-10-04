package com.project.shared.utils;

import java.util.Map;
import java.util.Map.Entry;

import com.project.shared.data.Pair;

public abstract class MapUtils {
    public static <K, V> Entry<K, V> findValue(Map<K, V> map, V value) {
        for (Entry<K, V> entry : map.entrySet()) {
            if (entry.getValue() != value) {
                continue;
            }
            return entry;
        }
        return null;
    }

    public static <K, V> boolean removeByValue(Map<K, V> map, V value) {
        Entry<K, V> entry = MapUtils.findValue(map, value);
        if (null == entry) {
            return false;
        }
        map.remove(entry.getKey());
        return true;
    }

    public static <K, V, M extends Map<K,V>> M putPairs(M map, Pair<K,V>[] keyValuePairs)
    {
        for (Pair<K,V> pair : keyValuePairs) {
            map.put(pair.getA(), pair.getB());
        }
        return map;
    }
}
