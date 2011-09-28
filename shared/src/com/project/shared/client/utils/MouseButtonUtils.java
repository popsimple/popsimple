package com.project.shared.client.utils;

import com.google.gwt.dom.client.NativeEvent;
import com.project.shared.data.MouseButtons;

public class MouseButtonUtils {


    public static MouseButtons fromNativeButton(int nativeButton)
    {
        switch (nativeButton)
        {
            case NativeEvent.BUTTON_LEFT:
                return MouseButtons.Left;
            case NativeEvent.BUTTON_MIDDLE:
                return MouseButtons.Middle;
            case NativeEvent.BUTTON_RIGHT:
                return MouseButtons.Right;
            default:
                return MouseButtons.Other;
        }
    }

    public static int toNativeButton(MouseButtons mouseButton)
    {
        switch (mouseButton)
        {
            case Left:
                return NativeEvent.BUTTON_LEFT;
            case Middle:
                return NativeEvent.BUTTON_MIDDLE;
            case Right:
                return NativeEvent.BUTTON_RIGHT;
            case Other:
            default:
                throw new UnsupportedMouseButtonException(mouseButton);
        }
    }
}
