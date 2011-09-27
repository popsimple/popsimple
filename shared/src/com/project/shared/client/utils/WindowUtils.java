package com.project.shared.client.utils;

import com.google.gwt.user.client.Window;
import com.project.shared.data.Point2D;

public class WindowUtils
{
    //#region Constants

    public static final String TARGET_NEW_TAB = "_blank";

    //#endregion

    public static Point2D getClientSize()
    {
        return new Point2D(Window.getClientWidth(), Window.getClientHeight());
    }

    public static void openNewTab(String url)
    {
        WindowUtils.openNewTabl(url, "");
    }

    public static void openNewTabl(String url, String features)
    {
        Window.open(url, TARGET_NEW_TAB, features);
    }
}
