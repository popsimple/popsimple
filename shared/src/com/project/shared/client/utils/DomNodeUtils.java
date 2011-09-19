package com.project.shared.client.utils;

import java.util.ArrayList;

import com.google.gwt.dom.client.Node;

public class DomNodeUtils
{
    public static Node getCommonAncestor(Node a, Node b)
    {
        if (a == b) {
            return a;
        }
        if ((null == a) || (null == b)) {
            return null;
        }

        ArrayList<Node> ancestorsA = DomNodeUtils.getAncestors(a);
        ArrayList<Node> ancestorsB = DomNodeUtils.getAncestors(b);
        ancestorsA.add(0, a);
        ancestorsB.add(0, b);

        for (Node nodeA : ancestorsA) {
            if (ancestorsB.contains(nodeA)) {
                return nodeA;
            }
        }
        return null;
    }

    public static ArrayList<Node> getAncestors(Node node)
    {
        ArrayList<Node> result = new ArrayList<Node>();
        while (true) {
            Node parent = node.getParentNode();
            if (null == parent) {
                return result;
            }
            result.add(parent);
        }
    }
}
