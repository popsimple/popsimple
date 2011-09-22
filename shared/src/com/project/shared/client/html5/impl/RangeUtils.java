package com.project.shared.client.html5.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.DOM;
import com.project.shared.client.html5.Range;
import com.project.shared.data.Pair;
import com.project.shared.data.funcs.Func;

public class RangeUtils
{
    public static void applyToNodesInRange(Range range, Func.Action<Element> func)
    {
        HashMap<Node, Boolean> nodeContainmentMap = getNodeContainmentMap(range);

        Element startElem = null;
        Element endElem = null;

        Pair<Node, Integer> startPoint = new Pair<Node,Integer>(range.getStartContainer(), range.getStartOffset());
        Pair<Node, Integer> endPoint = new Pair<Node,Integer>(range.getEndContainer(), range.getEndOffset());
        // Now modify the tree in-place, and at the same time remember the elements that define the range
        // after the modification.
        for (Map.Entry<Node, Boolean> entry : nodeContainmentMap.entrySet())
        {
            Element elem = wrapIncludedPart(startPoint, endPoint, entry.getKey(), entry.getValue());
            if (null != elem) {
                func.call(elem);
            }
            if (startPoint.getA() == entry.getKey()) {
                startElem = elem;
            }
            if (endPoint.getA() == entry.getKey()) {
                endElem = elem;
            }
        }

//        RangeImpl updatedRange = RangeImpl.create();
//        if ((null != startElem) && (null != endElem)) {
//            updatedRange.setStartBefore(startElem);
//            updatedRange.setEndAfter(endElem);
//        }
//        return updatedRange;
        if ((null != startElem) && (null != endElem)) {
            range.setStartBefore(startElem);
            range.setEndAfter(endElem);
        }
    }

    /**
     * Returns the set of nodes that are covered by the given range. For each node key, the boolean value indicates
     * whether the node is fully (true) or partially (false) contained in the range. 
     */
    public static HashMap<Node, Boolean> getNodeContainmentMap(Range range) {
        ArrayList<Node> descendants = new ArrayList<Node>();
        descendants.add(range.getCommonAncestorContainer());
        HashMap<Node, Boolean> nodeInclusionMap = new HashMap<Node, Boolean>();

        while (descendants.size() > 0) {
            for (Node descendant : descendants.toArray(new Node[0]))
            {
                descendants.remove(descendant);
                addNodeChildren(descendant, descendants);

                if (descendant.getNodeType() != Node.TEXT_NODE) {
                    continue;
                }

                int startOffsetCompare = range.comparePoint(descendant, 0);
                int endOffsetCompare = range.comparePoint(descendant, descendant.getNodeValue().length() - 1);

                boolean startContained = 0 == startOffsetCompare;
                boolean endContained = 0 == endOffsetCompare;
                boolean midContained = (-1 == startOffsetCompare) && (1 == endOffsetCompare);
                boolean isFullyContained = startContained && endContained;
                boolean isPartiallyContained = startContained || endContained || midContained;

                if (isPartiallyContained) {
                    //logNode("Checking descendant with offset compare value: " + startOffsetCompare, descendant);
                    // If we change the DOM while iterating here, the range.comparePoint method may return wrong results?
                    // that's why we add to a map and later split the elements appropriately
                    nodeInclusionMap.put(descendant, isFullyContained);
                }
            }
        }
        return nodeInclusionMap;
    }

    private static Element wrapIncludedPart(Pair<Node, Integer> startPoint, Pair<Node, Integer> endPoint, Node descendant, boolean fullyContained)
    {
        Element elem = null;
        Node startNode = startPoint.getA();
        Node endNode = endPoint.getA();
        int startOffset = startPoint.getB();
        int endOffset = endPoint.getB();

        if (startNode == descendant) {
            if (startNode == endNode) {
                elem = wrapSplitTextNode(startNode, startOffset, endOffset).mid;
            }
            else {
                elem = wrapSplitTextNode(startNode, startOffset, startNode.getNodeValue().length()).mid;
            }
        }
        else if (endNode == descendant) {
            if (startNode != endNode) {
                elem = wrapSplitTextNode(endNode, endOffset, endNode.getNodeValue().length()).pre;
            }
            else {
                elem = null;
            }
        }
        else if (fullyContained) {
            if (descendant.getNodeType() == Node.TEXT_NODE) {
                elem = wrapSplitTextNode(descendant, 0, descendant.getNodeValue().length()).parent;
            }
            else {
                elem = Element.as(descendant);
            }
        }
        return elem;
    }

    private static void addNodeChildren(Node commonAncestor, ArrayList<Node> descendants)
    {
        NodeList<Node> ancestorChildren = commonAncestor.getChildNodes();
        for (int i = 0 ; i < ancestorChildren.getLength(); i++)
        {
            Node item = ancestorChildren.getItem(i);
            //logNode("Adding child: ", item);
            descendants.add(item);
        }
    }

//    private static void logNode(String message, Node item)
//    {
//        Logger.log(message + " - Node: " + item.toString() + " : " + item.getNodeValue());
//        if (item.getNodeType() == Node.ELEMENT_NODE) {
//            Element elem = Element.as(item);
//            Logger.log("   Node is an Element:" + elem.getString());
//        }
//    }

    public static class SplitElement {
        public SplitElement(Element parent, Element pre, Element mid, Element post)
        {
            super();
            this.parent = parent;
            this.pre = pre;
            this.mid = mid;
            this.post = post;
        }

        Element parent;
        Element pre;
        Element mid;
        Element post;
    }

    public static SplitElement wrapSplitTextNode(Node textNode, int startOffset, int endOffset)
    {
        com.google.gwt.user.client.Element wrapperSpan = DOM.createSpan();
        String text = textNode.getNodeValue();
        boolean endOffsetBeyondLength = text.length() <= endOffset;

        com.google.gwt.user.client.Element prePartSpan = null;
        com.google.gwt.user.client.Element midPartSpan = null;
        com.google.gwt.user.client.Element postPartSpan = null;

        if (startOffset > 0) {
            prePartSpan = DOM.createSpan();
            prePartSpan.setInnerText(text.substring(0, startOffset));
            wrapperSpan.appendChild(prePartSpan);
        }

        midPartSpan = DOM.createSpan();
        midPartSpan.setInnerText(text.substring(startOffset, endOffset));
        wrapperSpan.appendChild(midPartSpan);

        if (false == endOffsetBeyondLength) {
            postPartSpan = DOM.createSpan();
            postPartSpan.setInnerText(text.substring(endOffset));
            wrapperSpan.appendChild(postPartSpan);
        }
        textNode.getParentNode().replaceChild(wrapperSpan, textNode);
        return new SplitElement(wrapperSpan, prePartSpan, midPartSpan, postPartSpan);
    }
}
