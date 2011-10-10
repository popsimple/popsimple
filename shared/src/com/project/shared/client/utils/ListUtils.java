package com.project.shared.client.utils;

import java.util.ArrayList;

public class ListUtils
{
    public static final <T> T getNext(ArrayList<T> list, T element, boolean isCyclic)
    {
        int index = list.indexOf(element);
        if (-1 == index)
        {
            return null;
        }
        if (index == (list.size() - 1))
        {
            if (isCyclic)
            {
                return ListUtils.getFirst(list);
            }
            else
            {
                return element;
            }
        }
        else
        {
            return list.get(index + 1);
        }
    }

    public static final <T> T getPrevious(ArrayList<T> list, T element, boolean isCyclic)
    {
        int index = list.indexOf(element);
        if (-1 == index)
        {
            return null;
        }
        if (index == 0)
        {
            if (isCyclic)
            {
                return ListUtils.getLast(list);
            }
            else
            {
                return element;
            }
        }
        else
        {
            return list.get(index - 1);
        }
    }

    public static final <T> T getFirst(ArrayList<T> list)
    {
        if (list.isEmpty())
        {
            return null;
        }
        return list.get(0);
    }

    public static final <T> T getLast(ArrayList<T> list)
    {
        if (list.isEmpty())
        {
            return null;
        }
        return list.get(list.size() - 1);
    }

    public static final <T> boolean isLast(ArrayList<T> list, T element)
    {
        int index = list.indexOf(element);
        if (-1 == index)
        {
            return false;
        }
        return (list.size() - 1) == index;
    }
}
