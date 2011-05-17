package com.project.canvas.client.shared;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.google.gwt.dom.client.Element;

//TODO: Think about a better design. 
public abstract class ZIndexAllocator
{
	private static int nextZIndex = 1;
	private static TreeMap<Integer, Element> _zIndexMap = new TreeMap<Integer, Element>();
	
	public static void reset()
	{
		nextZIndex = 1;
		_zIndexMap.clear();
	}
	
	public static int allocateSetZIndex(Element element)
	{
		int allocatedZIndex = nextZIndex;
		nextZIndex += 1;
		
		element.getStyle().setZIndex(allocatedZIndex);
		_zIndexMap.put(allocatedZIndex, element);
		return allocatedZIndex;
	}
	
	public static void deallocateZIndex(Element element)
	{
		MapExtensions.removeByValue(_zIndexMap, element);
	}
	
	public static int getElementZIndex(Element element)
	{
		Entry<Integer, Element> entry = MapExtensions.findValue(_zIndexMap, element);
		if (null == entry)
		{
			return 0;
		}
		return entry.getKey();
	}
	
	//TODO: What if the element does'nt have a zindex? it will return 0 and then we'll have duplicate.
	public static void switchZIndex(Element element1, Element element2)
	{
		int zIndex1 = ZIndexAllocator.getElementZIndex(element1);
		int zIndex2 = ZIndexAllocator.getElementZIndex(element2);
		
		MapExtensions.removeByValue(_zIndexMap, element1);
		MapExtensions.removeByValue(_zIndexMap, element2);
		
		element1.getStyle().setZIndex(zIndex2);
		element2.getStyle().setZIndex(zIndex1);
		_zIndexMap.put(zIndex2, element1);
		_zIndexMap.put(zIndex1, element2);
	}
	
	public static void moveElementBelow(Element element)
	{
		Element belowElement = getBelowOverlappingElement(element);
		if (null == belowElement)
		{
			return;
		}
		switchZIndex(element, belowElement);
	}
	
	public static void moveElementAbove(Element element)
	{
		Element aboveElement = getAboveOverlappingElement(element);
		if (null == aboveElement)
		{
			return;
		}
		switchZIndex(element, aboveElement);
	}
	
	private static Element getAboveOverlappingElement(Element element)
	{
		int currentZIndex = getElementZIndex(element);
		for (Entry<Integer, Element> entry : _zIndexMap.tailMap(currentZIndex).entrySet())
		{
			Element nextElement = entry.getValue();
			if (element == nextElement)
			{
				continue;
			}
			if (false == ElementExtensions.isOverlappingElements(element, nextElement))
			{
				continue;
			}
			return nextElement;
		}
		return null;
	}
	
	private static Element getBelowOverlappingElement(Element element)
	{
		int currentZIndex = getElementZIndex(element);
		
		ArrayList<Entry<Integer, Element>> array = new ArrayList<Map.Entry<Integer,Element>>(
				_zIndexMap.headMap(currentZIndex).entrySet());
		for (int index = array.size() - 1; index >= 0; index--) 
		{
			Element nextElement = array.get(index).getValue();
			if (element == nextElement)
			{
				continue;
			}
			if (false == ElementExtensions.isOverlappingElements(element, nextElement))
			{
				continue;
			}
			return nextElement;
		}
		return null;
	}
	
	public static int getLastAllocatedZIndex()
	{
		return nextZIndex - 1;
	}
	
	public static int getTopMostZIndex()
	{
		return nextZIndex;
	}
}
