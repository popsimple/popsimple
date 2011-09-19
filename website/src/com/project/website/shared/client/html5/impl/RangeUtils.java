package com.project.website.shared.client.html5.impl;

import java.util.ArrayList;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.DOM;
import com.project.shared.data.funcs.Func;
import com.project.shared.utils.loggers.Logger;
import com.project.website.shared.client.html5.Range;

public class RangeUtils
{
    public static void applyToNodesInRange(Range range, Func.Action<Element> func)
    {
        Node commonAncestor = range.getCommonAncestorContainer();

        final Node startNode = range.getStartContainer();
        final Node endNode = range.getEndContainer();

        ArrayList<Node> descendants = new ArrayList<Node>();
        addNodeChildren(commonAncestor, descendants);
        while (descendants.size() > 0) {
            for (Node descendant : descendants.toArray(new Node[0]))
            {
                descendants.remove(descendant);
                addNodeChildren(descendant, descendants);

                // Node is partially contained.
                // Assumption: only start and end containers need to be split,
                // all other nodes will either be fully contained, or partially
                // contained with the selection ending inside one of their
                // descendants.
                int startOffsetCompare = range.comparePoint(descendant, 0);
                Logger.log("offsetCompare: " + startOffsetCompare  + " -  " + descendant.toString() + " : " + descendant.getNodeValue());
                logNode("Checking descendant: ", descendant);

                Element elem = wrapIncludedPart(range, startNode, endNode, descendant, startOffsetCompare);

                if (null != elem) {
                    func.call(elem);
                }

            }
        }
    }

    private static Element wrapIncludedPart(Range range, final Node startNode, final Node endNode, Node descendant, int startOffsetCompare)
    {
        Element elem = null;
        if (startNode == descendant) {
            if (startNode == endNode) {
                elem = wrapSplitTextNode(startNode, range.getStartOffset(), range.getEndOffset()).mid;
            }
            else {
                elem = wrapSplitTextNode(startNode, range.getStartOffset(), startNode.getNodeValue().length()).post;
            }
        }
        else if (endNode == descendant) {
            if (startNode != endNode) {
                elem = wrapSplitTextNode(endNode, range.getEndOffset(), endNode.getNodeValue().length()).pre;
            }
            else {
                elem = null;
            }
        }
        else if (0 == startOffsetCompare) {
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
            logNode("Adding child: ", item);
            descendants.add(item);
        }
    }

    private static void logNode(String message, Node item)
    {
        Logger.log(message + " - Node: " + item.toString() + " : " + item.getNodeValue());
        if (item.getNodeType() == Node.ELEMENT_NODE) {
            Element elem = Element.as(item);
            Logger.log("   Node is an Element:" + elem.getString());
        }
    }

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
        boolean endOffsetBeyondLength = text.length() >= endOffset;

        com.google.gwt.user.client.Element prePartSpan = null;
        com.google.gwt.user.client.Element midPartSpan = null;
        com.google.gwt.user.client.Element postPartSpan = null;

        if ((0 == startOffset) && endOffsetBeyondLength) {
            wrapperSpan.setInnerText(textNode.getNodeValue());
        }
        else {

            prePartSpan = DOM.createSpan();
            prePartSpan.setInnerText(text.substring(0, startOffset));
            wrapperSpan.appendChild(prePartSpan);

            midPartSpan = DOM.createSpan();
            midPartSpan.setInnerText(text.substring(startOffset, endOffset));
            wrapperSpan.appendChild(midPartSpan);

            if (false == endOffsetBeyondLength) {
                postPartSpan = DOM.createSpan();
                postPartSpan.setInnerText(text.substring(endOffset));
                wrapperSpan.appendChild(postPartSpan);
            }
        }
        textNode.getParentNode().replaceChild(wrapperSpan, textNode);

        if (null != postPartSpan) {
            return new SplitElement(wrapperSpan, prePartSpan, midPartSpan, postPartSpan);
        }
        else {
            return new SplitElement(wrapperSpan, prePartSpan, null, midPartSpan);
        }
    }
}
