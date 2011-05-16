package com.project.canvas.client.shared;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;


public class ElementWrapper extends Widget {
	public ElementWrapper(Element elem) {
		this.setElement(elem);
	}
}
