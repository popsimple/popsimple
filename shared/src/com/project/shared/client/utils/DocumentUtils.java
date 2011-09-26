package com.project.shared.client.utils;

import com.google.gwt.dom.client.Element;
import com.project.shared.utils.loggers.Logger;

public class DocumentUtils
{
    /* part of HTML5 and was supported before also: http://www.w3.org/TR/html5/editing.html#dom-document-activeelement */
    public static final native Element getActiveElement() /*-{
        return $wnd.document.activeElement;
    }-*/;


    public static boolean isActiveElementTree(Element rootElem)
    {
        Element element = DocumentUtils.getActiveElement();
        while (null != element) {
            if (rootElem == element) {
                return true;
            }
            element = element.getParentElement();
        }
        return false;
    }
}
