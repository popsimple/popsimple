package com.project.website.canvas.client.shared;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.google.gwt.dom.client.Element;
import com.project.shared.client.utils.ElementUtils;
import com.project.shared.utils.MapUtils;

//TODO: Think about a better design.
public abstract class ZIndexAllocator {
    private static int _nextZIndex = 1;
    private static TreeMap<Integer, Element> _zIndexMap = new TreeMap<Integer, Element>();

    public static void reset() {
        _nextZIndex = 1;
        _zIndexMap.clear();
    }

    public static int allocateSetZIndex(Element element) {
        int allocatedZIndex = _nextZIndex;
        _nextZIndex += 1;

        element.getStyle().setZIndex(allocatedZIndex);
        _zIndexMap.put(allocatedZIndex, element);
        return allocatedZIndex;
    }

    public static void deallocateZIndex(Element element) {
        MapUtils.removeByValue(_zIndexMap, element);
    }

    public static int getElementZIndex(Element element) {
        Entry<Integer, Element> entry = MapUtils.findValue(_zIndexMap, element);
        if (null == entry) {
            return 0;
        }
        return entry.getKey();
    }

    // TODO: What if the element does'nt have a zindex? it will return 0 and
    // then we'll have duplicate.
    public static void switchZIndex(Element element1, Element element2) {
        int zIndex1 = ZIndexAllocator.getElementZIndex(element1);
        int zIndex2 = ZIndexAllocator.getElementZIndex(element2);

        MapUtils.removeByValue(_zIndexMap, element1);
        MapUtils.removeByValue(_zIndexMap, element2);

        element1.getStyle().setZIndex(zIndex2);
        element2.getStyle().setZIndex(zIndex1);
        _zIndexMap.put(zIndex2, element1);
        _zIndexMap.put(zIndex1, element2);
    }

    public static void moveElementBelow(Element element) {
        ArrayList<Element> belowElements = getElementsToBelowOverlapping(element);
        if (null == belowElements) {
            // TODO: Decrease the ZIndex of that element by 1 according to the
            // ZIndexAllocator.
            return;
        }
        for (Element nextElement : belowElements) {
            switchZIndex(element, nextElement);
        }
    }

    public static void moveElementAbove(Element element) {
        ArrayList<Element> aboveElement = getElementsToAboveOverlapping(element);
        if (null == aboveElement) {
            // TODO: Increase the ZIndex of that element by 1 according to the
            // ZIndexAllocator.
            return;
        }
        for (Element nextElement : aboveElement) {
            switchZIndex(element, nextElement);
        }
    }

    private static ArrayList<Element> getElementsToAboveOverlapping(Element element) {
        ArrayList<Element> elements = new ArrayList<Element>();

        int currentZIndex = getElementZIndex(element);
        for (Entry<Integer, Element> entry : _zIndexMap.tailMap(currentZIndex).entrySet()) {
            Element nextElement = entry.getValue();
            if (element == nextElement) {
                continue;
            }
            if (false == ElementUtils.areOverlappingElements(element, nextElement)) {
                elements.add(nextElement);
                continue;
            }
            elements.add(nextElement);
            return elements;
        }
        return null;
    }

    private static ArrayList<Element> getElementsToBelowOverlapping(Element element) {
        ArrayList<Element> fooElements = new ArrayList<Element>();
        int currentZIndex = getElementZIndex(element);

        ArrayList<Entry<Integer, Element>> array = new ArrayList<Map.Entry<Integer, Element>>(_zIndexMap
                .headMap(currentZIndex).entrySet());
        for (int index = array.size() - 1; index >= 0; index--) {
            Element nextElement = array.get(index).getValue();
            if (element == nextElement) {
                continue;
            }
            if (false == ElementUtils.areOverlappingElements(element, nextElement)) {
                fooElements.add(nextElement);
                continue;
            }
            fooElements.add(nextElement);
            return fooElements;
        }
        return null;
    }

    public static int getLastAllocatedZIndex() {
        return _nextZIndex - 1;
    }

    public static int getTopMostZIndex() {
        return _nextZIndex;
    }
}
