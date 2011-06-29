package com.project.website.canvas.client.shared.widgets;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.SimplePanel;

public class ElementWrapper extends SimplePanel {
    public ElementWrapper(Element elem) {
        super(elem);
    }
    public static ElementWrapper of(Element elem) {
        return new ElementWrapper(elem);
    }
}
