package com.project.website.canvas.client.canvastools.textedit;

import com.google.gwt.dom.client.Element;

public interface ToolbarButtonInfo
{
    boolean isSet(Element elem);
    void set(Element elem);
    void unset(Element elem);
    boolean isOnRootElemOnly();
    
    /**
     * @param testedElement may be null, indicating no element is focused
     */
    void updateButtonStatus(Element testedElement);
}
