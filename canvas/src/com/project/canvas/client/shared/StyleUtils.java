package com.project.canvas.client.shared;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;

public class StyleUtils
{
    public static final String buildBackgroundUrl(String imageUrl)
    {
        return "url(\"" + imageUrl + "\")";
    }

    public static void setBackgroundRepeat(Style style, boolean repeat)
    {
        if (repeat)
        {
            style.setProperty("backgroundRepeat", "repeat");
        }
        else
        {
            style.setProperty("backgroundRepeat", "no-repeat");
        }
    }

    public static void setBackgroundCenter(Style style)
    {
        style.setProperty("backgroundPosition", "center center");
    }

    public static void setBackgroundStretch(Style style, boolean stretchWidth, boolean stretchHeight)
    {
        String width = "";
        if (stretchWidth)
        {
            width = "100%";
        }
        else
        {
            width = "auto";
        }
        String height = "";
        if (stretchHeight)
        {
            height = "100%";
        }
        else
        {
            height = "auto";
        }
        style.setProperty("backgroundSize", width + " " + height);
    }
}
