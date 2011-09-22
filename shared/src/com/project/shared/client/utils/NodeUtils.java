package com.project.shared.client.utils;

import java.util.ArrayList;

import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;

public class NodeUtils
{

    public static <T extends Node> ArrayList<T> fromNodeList(NodeList<T> childNodes)
    {
        ArrayList<T> res = new ArrayList<T>();
        for (int i = 0 ; i < childNodes.getLength(); i++)
        {
            res.add(childNodes.getItem(i));
        }
        return res;
    }
}
