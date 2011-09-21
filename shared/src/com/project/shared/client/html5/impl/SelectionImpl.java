package com.project.shared.client.html5.impl;


import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Node;
import com.project.shared.client.html5.Range;
import com.project.shared.client.html5.Selection;

public class SelectionImpl extends JavaScriptObject implements Selection
{
    protected SelectionImpl() {}

    public native final static SelectionImpl getWindowSelection()
    /*-{
        return $wnd.getSelection();
    }-*/;

    @Override
    public final native Node getAnchorNode()
    /*-{
        return this.anchorNode;
    }-*/;

    @Override
    public final native int getAnchorOffset()
    /*-{
        return this.anchorOffset;
    }-*/;

    @Override
    public final native Node getFocusNode()
    /*-{
        return this.focusNode;
    }-*/;

    @Override
    public final native int getFocusOffset()
    /*-{
        return this.focusOffset;
    }-*/;

    @Override
    public final native boolean isCollapsed()
    /*-{
        return this.isCollapsed;
    }-*/;

    @Override
    public final native void collapse(Node parentNode, int offset)
    /*-{
        this.collapse(parentNode, offset);
    }-*/;

    @Override
    public final native void collapseToStart()
    /*-{
        this.collapseToStart();
    }-*/;

    @Override
    public final native void collapseToEnd()
    /*-{
        this.collapseToEnd();
    }-*/;

    @Override
    public final native void extend(Node parentNode, int offset)
    /*-{
        this.extend(parentNode, offset);
    }-*/;

    @Override
    public final native void modify(String alter, String direction, String granularity)
    /*-{
        this.modify(alter, direction, granularity);
    }-*/;

    @Override
    public final native void selectAllChildren(Node parentNode)
    /*-{
        this.selectAllChildren(parentNode);
    }-*/;

    @Override
    public final native void deleteFromDocument()
    /*-{
        this.deleteFromDocument();
    }-*/;

    @Override
    public final native int getRangeCount()
    /*-{
        return this.rangeCount;
    }-*/;

    @Override
    public final native Range getRangeAt(int index)
    /*-{
        return this.getRangeAt(index);
    }-*/;

    @Override
    public final void addRange(Range range)
    {
        if (range instanceof RangeImpl) {
            this.addRangeNative((RangeImpl)range);
            return;
        }
        throw new RuntimeException("Implemented only for Range class: " + RangeImpl.class.getName());
    }

    public final native void addRangeNative(RangeImpl range)
    /*-{
        this.addRange(range);
    }-*/;

    @Override
    public final void removeRange(Range range)
    {
        if (range instanceof RangeImpl) {
            this.removeRangeNative((RangeImpl)range);
            return;
        }
        throw new RuntimeException("Implemented only for Range class: " + RangeImpl.class.getName());
    }

    public final native void removeRangeNative(RangeImpl range)
    /*-{
        this.removeRange(range);
    }-*/;

    @Override
    public final native void removeAllRanges()
    /*-{
        this.removeAllRanges();
    }-*/;

}
