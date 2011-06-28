package com.project.shared.client.utils;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;

public class NativeUtils
{
    public native static void disableTextSelectInternal(Element e, boolean disable)/*-{
		if (disable) {
			e.ondrag = function() {
				return false;
			};
			e.onselectstart = function() {
				return false;
			};
			e.style.MozUserSelect = "none"
		} else {
			e.ondrag = null;
			e.onselectstart = null;
			e.style.MozUserSelect = "text"
		}
    }-*/;

    public static boolean keyIsEnter(KeyPressEvent event)
    {
        return event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER;
    }
}
