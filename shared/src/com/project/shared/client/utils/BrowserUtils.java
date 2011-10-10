package com.project.shared.client.utils;

import com.google.gwt.event.dom.client.TouchEvent;


public class BrowserUtils
{
    private static final boolean IS_CHROME = BrowserDetect.get().getBrowser().equals("Chrome");
    private static final boolean IS_SAFARI = BrowserDetect.get().getBrowser().equals("Safari");
    private static final boolean IS_IE = BrowserDetect.get().getBrowser().equals("Explorer");

    public static boolean isChrome() {
        return IS_CHROME;
    }

    public static boolean isSafari() {
        return IS_SAFARI;
    }

    /**
     * Returns true if the browser supports animations inside svg elements
     */
    public static boolean supportsDynamicSVG()
    {
        return false == BrowserUtils.isChrome();
    }

    /**
     * Return true if the browser supports cursor: none
     */
    public static boolean supportCSSCursorNone()
    {
        // TODO: check exactly which versions of safari don't support cursor: none
        return false == BrowserUtils.isSafari();
    }

    public static boolean isIE()
    {
        return IS_IE;
    }

    public static boolean isTouchSupported()
    {
        return TouchEvent.isSupported();
    }

    public static boolean isMainInterfaceTouch()
    {
        return BrowserUtils.isTouchSupported()
            || BrowserDetect.get().getOS().equals("iPhone/iPod")
            || BrowserDetect.get().getOS().equals("Android");
    }

}
