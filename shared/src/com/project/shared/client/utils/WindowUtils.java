package com.project.shared.client.utils;

import com.google.gwt.user.client.Window;
import com.project.shared.data.Point2D;

public class WindowUtils
{
    public static Point2D getClientSize()
    {
        return new Point2D(Window.getClientWidth(), Window.getClientHeight());
    }
}
