package com.project.shared.client.utils;

import com.google.gwt.dom.client.Element;

public class DocumentUtils
{
    /* part of HTML5 and was supported before also: http://www.w3.org/TR/html5/editing.html#dom-document-activeelement */
    public static final native Element getActiveElement() /*-{
        return $wnd.document.activeElement;
    }-*/;
}
