package com.project.canvas.client.shared;

import java.util.Map;
import java.util.Map.Entry;

public abstract class MapExtensions 
{
	public static <K, V> Entry<K, V> findValue(Map<K, V> map, V value)
	{
		for (Entry<K, V> entry : map.entrySet())
		{
			if (entry.getValue() != value)
			{
				continue;
			}
			return entry;
		}
		return null;
	}
	
	public static <K, V> boolean removeByValue(Map<K, V> map, V value)
	{
		Entry<K, V> entry = MapExtensions.findValue(map, value);
		if (null == entry)
		{
			return false;
		}
		map.remove(entry.getKey());
		return true;
	}
}
