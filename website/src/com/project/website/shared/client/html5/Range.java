package com.project.website.shared.client.html5;

import com.google.gwt.dom.client.Node;

/* Range IDL from http://dvcs.w3.org/hg/domcore/raw-file/tip/Overview.html#range
 * interface Range {
 readonly attribute Node startContainer;
 readonly attribute unsigned long startOffset;
 readonly attribute Node endContainer;
 readonly attribute unsigned long endOffset;
 readonly attribute boolean collapsed;
 readonly attribute Node commonAncestorContainer;

 void setStart(Node refNode, unsigned long offset);
 void setEnd(Node refNode, unsigned long offset);
 void setStartBefore(Node refNode);
 void setStartAfter(Node refNode);
 void setEndBefore(Node refNode);
 void setEndAfter(Node refNode);
 void collapse(boolean toStart);
 void selectNode(Node refNode);
 void selectNodeContents(Node refNode);

 enum CompareHow : unsigned short {
 START_TO_START = 0,
 START_TO_END   = 1,
 END_TO_END     = 2,
 END_TO_START   = 3
 };

 short compareBoundaryPoints(CompareHow how, Range sourceRange);
 void deleteContents()
 DocumentFragment extractContents();
 DocumentFragment cloneContents();
 void insertNode(Node newNode);
 void surroundContents(Node newParent);
 Range cloneRange();
 void detach();

 boolean isPointInRange(Node parent, unsigned long offset);
 short comparePoint(Node parent, unsigned long offset);

 boolean intersectsNode(Node node);

 stringifier;
 };*/
public interface Range
{
    Node getStartContainer();
    long getStartOffset();
    Node getEndContainer();
    long getEndOffset();

    boolean getCollapsed();

    Node getCommonAncestorContainer();

    void setStart(Node refNode, long offset);
    void setEnd(Node refNode, long offset);
    void setStartBefore(Node refNode);
    void setStartAfter(Node refNode);
    void setEndBefore(Node refNode);
    void setEndAfter(Node refNode);

    void collapse(boolean toStart);
    void selectNode(Node refNode);
    void selectNodeContents(Node refNode);

    public static enum CompareHow {
        START_TO_START(0),
        START_TO_END(1),
        END_TO_END(2),
        END_TO_START(3);

        @SuppressWarnings("unused")
        private int _value;

        CompareHow(int i)
        {
            this._value = i;
        }
    };

    short compareBoundaryPoints(CompareHow how, Range sourceRange);
    void deleteContents();
    // We can't use import com.google.gwt.xml.client.DocumentFragment;
    // because it references the gwt.xml Node which is not compatible with dom.Node
    //DocumentFragment extractContents();
    //DocumentFragment cloneContents();
    Node extractContents();
    Node cloneContents();

    void insertNode(Node newNode);
    void surroundContents(Node newParent);
    Range cloneRange();
    void detach();
    boolean isPointInRange(Node parent, long offset);
    short comparePoint(Node parent, long offset);
    boolean intersectsNode(Node node);
}
