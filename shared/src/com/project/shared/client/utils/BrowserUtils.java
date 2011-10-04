package com.project.shared.client.utils;

import com.google.gwt.user.client.Window.Navigator;

public class BrowserUtils
{
    public static boolean isChrome() {
        return Navigator.getUserAgent().toLowerCase().contains("chrome");
    }

    public static boolean supportsDynamicSVG()
    {
        return false == BrowserUtils.isChrome();
    }
}
