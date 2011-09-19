package com.project.website.shared.client.html5;

import com.google.gwt.dom.client.Node;

/* Range IDL from http://dvcs.w3.org/hg/domcore/raw-file/tip/Overview.html#range
 * interface Range {
 readonly attribute Node startContainer;
 readonly attribute unsigned int startOffset;
 readonly attribute Node endContainer;
 readonly attribute unsigned int endOffset;
 readonly attribute boolean collapsed;
 readonly attribute Node commonAncestorContainer;

 void setStart(Node refNode, unsigned int offset);
 void setEnd(Node refNode, unsigned int offset);
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

 boolean isPointInRange(Node parent, unsigned int offset);
 short comparePoint(Node parent, unsigned int offset);

 boolean intersectsNode(Node node);

 stringifier;
 };*/
public interface Range
{
    Node getStartContainer();
    int getStartOffset();
    Node getEndContainer();
    int getEndOffset();

    boolean getCollapsed();

    Node getCommonAncestorContainer();

    void setStart(Node refNode, int offset);
    void setEnd(Node refNode, int offset);
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
    boolean isPointInRange(Node parent, int offset);
    short comparePoint(Node parent, int offset);
    boolean intersectsNode(Node node);
}
