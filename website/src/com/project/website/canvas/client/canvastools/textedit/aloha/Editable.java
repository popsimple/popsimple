package com.project.website.canvas.client.canvastools.textedit.aloha;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

public class Editable extends JavaScriptObject
{
    protected Editable() {}

    public final native void activate()
    /*-{
        this.activate();
    }-*/;

    public final native void disable()
    /*-{
        this.disable();
    }-*/;

    public final native void enable()
    /*-{
        this.enable();
    }-*/;

    public final native String getContents()
    /*-{
        return this.getContents();
    }-*/;

    public final native boolean isDisabled()
    /*-{
        return this.isDisabled();
    }-*/;

    public final native boolean isModified()
    /*-{
        return this.isModified();
    }-*/;

    public final native void setUnmodified()
    /*-{
        this.setUnmodified();
    }-*/;

    public final native String getId()
    /*-{
        return this.getId();
    }-*/;

    public final Element getElement()
    {
        return DOM.getElementById(this.getId());
    }

    public final void setContents(String text)
    {
        this.getElement().setInnerHTML(text);
    }
}


