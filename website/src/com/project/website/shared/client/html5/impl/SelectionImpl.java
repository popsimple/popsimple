package com.project.website.shared.client.html5.impl;

import org.apache.commons.lang.NotImplementedException;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Node;
import com.project.website.shared.client.html5.Range;
import com.project.website.shared.client.html5.Selection;

public class SelectionImpl extends JavaScriptObject implements Selection
{
    public native final SelectionImpl getWindowSelection()
    /*-{
        return window.getSelection();
    }-*/;

    @Override
    public final native Node getAnchorNode()
    /*-{
        return this.getAnchorNode();
    }-*/;

    @Override
    public final native long getAnchorOffset()
    /*-{
        return this.getAnchorOffset();
    }-*/;

    @Override
    public final native Node getFocusNode()
    /*-{
        return this.getFocusNode();
    }-*/;

    @Override
    public final native long getFocusOffset()
    /*-{
        return this.getFocusOffset();
    }-*/;

    @Override
    public final native boolean isCollapsed()
    /*-{
        return this.isCollapsed();
    }-*/;

    @Override
    public final native void collapse(Node parentNode, long offset)
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
    public final native void extend(Node parentNode, long offset)
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
    public final native long getRangeCount()
    /*-{
        return this.getRangeCount();
    }-*/;

    @Override
    public final native Range getRangeAt(long index)
    /*-{
        return this.getRangeAt(index);
    }-*/;

    @Override
    public void addRange(Range range)
    {
        if (range instanceof RangeImpl) {
            this.addRangeNative((RangeImpl)range);
        }
        throw new NotImplementedException("Implemented only for Range class: " + RangeImpl.class.getCanonicalName());
    }

    public final native void addRangeNative(RangeImpl range)
    /*-{
        this.addRange(range);
    }-*/;

    @Override
    public void removeRange(Range range)
    {
        if (range instanceof RangeImpl) {
            this.removeRangeNative((RangeImpl)range);
        }
        throw new NotImplementedException("Implemented only for Range class: " + RangeImpl.class.getCanonicalName());
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
