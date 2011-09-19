package com.project.shared.client.html5.impl;


import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Node;
import com.project.shared.client.html5.Range;

public class RangeImpl extends JavaScriptObject implements Range
{
    protected RangeImpl() {}

    @Override
    public final native Node getStartContainer()
    /*-{
        return this.startContainer;
    }-*/;

    @Override
    public final native int getStartOffset()
    /*-{
        return this.startOffset;
    }-*/;

    @Override
    public final native Node getEndContainer()
    /*-{
        return this.endContainer;
    }-*/;

    @Override
    public final native int getEndOffset()
    /*-{
        return this.endOffset;
    }-*/;

    @Override
    public final native boolean getCollapsed()
    /*-{
        return this.collapsed;
    }-*/;

    @Override
    public final native Node getCommonAncestorContainer()
    /*-{
        return this.commonAncestorContainer;
    }-*/;

    @Override
    public final native void setStart(Node refNode, int offset)
    /*-{
        this.setStart(refNode, offset);
    }-*/;

    @Override
    public final native void setEnd(Node refNode, int offset)
    /*-{
        this.setEnd(refNode, offset);
    }-*/;

    @Override
    public final native void setStartBefore(Node refNode)
    /*-{
        this.setStartBefore(refNode);
    }-*/;

    @Override
    public final native void setStartAfter(Node refNode)
    /*-{
        this.setStartAfter(refNode);
    }-*/;

    @Override
    public final native void setEndBefore(Node refNode)
    /*-{
        this.setEndBefore(refNode);
    }-*/;

    @Override
    public final native void setEndAfter(Node refNode)
    /*-{
        this.setEndAfter(refNode);
    }-*/;

    @Override
    public final native void collapse(boolean toStart)
    /*-{
        this.collapse(toStart);
    }-*/;

    @Override
    public final native void selectNode(Node refNode)
    /*-{
        this.selectNode(refNode);
    }-*/;

    @Override
    public final native void selectNodeContents(Node refNode)
    /*-{
        this.selectNodeContents(refNode);
    }-*/;

    @Override
    public final short compareBoundaryPoints(CompareHow how, Range sourceRange)
    {
        if (sourceRange instanceof RangeImpl) {
            return this.compareBoundaryPointsNative(how, (RangeImpl)sourceRange);
        }
        throw new RuntimeException("Only implemented for Range class: " + RangeImpl.class.getName());
    }

    public final native short compareBoundaryPointsNative(CompareHow how, RangeImpl sourceRange)
    /*-{
        return this.compareBoundaryPoints(how, sourceRange);
    }-*/;

    @Override
    public final native void deleteContents()
    /*-{
        this.deleteContents();
    }-*/;

    @Override
    public final native Node extractContents()
    /*-{
        return this.extractContents();
    }-*/;

    @Override
    public final native Node cloneContents()
    /*-{
        return this.cloneContents();
    }-*/;

    @Override
    public final native void insertNode(Node newNode)
    /*-{
        this.insetNode(newNode);
    }-*/;

    @Override
    public final native void surroundContents(Node newParent)
    /*-{
        this.surroundContents(newParent);
    }-*/;

    @Override
    public final native Range cloneRange()
    /*-{
        return this.cloneRange();
    }-*/;

    @Override
    public final native void detach()
    /*-{
        this.detach();
    }-*/;

    @Override
    public final native boolean isPointInRange(Node parent, int offset)
    /*-{
        return this.isPointInRange(parent, offset);
    }-*/;

    @Override
    public final native short comparePoint(Node parent, int offset)
    /*-{
        if (!this.comparePoint) {
            // IE 9 /  IERange doesn't implement this.
            var tempRange = $wnd.document.createRange();
            tempRange.setStart(parent, offset);
            tempRange.setEnd(parent, offset+1);
            var startToStart = this.compareBoundaryPoints($wnd.Range.START_TO_START, tempRange);
            var startToEnd = this.compareBoundaryPoints($wnd.Range.START_TO_END, tempRange);
            var onStartOrAfter = 0 >= startToStart;
            var onEndOrBefore = 0 <= startToEnd;
            if (onStartOrAfter) {
                if (onEndOrBefore) {
                    return 0;
                }
                return 1;
            }
            else  {
                return -1;
            }
        }
        return this.comparePoint(parent, offset);
    }-*/;

    @Override
    public final native boolean intersectsNode(Node node)
    /*-{
        return this.intersectsNode(node);
    }-*/;

}
