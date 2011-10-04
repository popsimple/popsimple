package com.project.shared.client.utils;

import com.google.gwt.user.client.Window.Navigator;

public class BrowserUtils
{
    private static final boolean IS_CHROME = Navigator.getUserAgent().toLowerCase().contains("chrome");

    public static boolean isChrome() {
        return IS_CHROME;
    }

    public static boolean supportsDynamicSVG()
    {
        return false == BrowserUtils.isChrome();
    }
}
