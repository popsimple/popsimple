package com.project.canvas.client.shared;

import com.google.gwt.dom.client.Style;

public class StyleUtils
{
    public static final String buildBackgroundUrl(String imageUrl)
    {
        return "url(\"" + imageUrl + "\")";
    }

    public static void clearBackgroundRepeat(Style style)
    {
        style.clearProperty(CssProperties.BACKGROUND_REPEAT);
    }

    public static void setBackgroundRepeat(Style style, boolean repeat)
    {
        if (repeat)
        {
            style.setProperty(CssProperties.BACKGROUND_REPEAT, "repeat");
        }
        else
        {
            style.setProperty(CssProperties.BACKGROUND_REPEAT, "no-repeat");
        }
    }

    public static void clearBackgroundPosition(Style style)
    {
        style.clearProperty(CssProperties.BACKGROUND_POSITION);
    }

    public static void setBackgroundCenter(Style style)
    {
        style.setProperty(CssProperties.BACKGROUND_POSITION, "center center");
    }

    public static void clearBackgroundSize(Style style)
    {
        style.clearProperty(CssProperties.BACKGROUND_SIZE);
    }

    public static void setBackgroundStretch(Style style, boolean stretchWidth, boolean stretchHeight)
    {
        String width = "";
        if (stretchWidth){
            width = "100%";
        }
        else{
            width = "auto";
        }
        String height = "";
        if (stretchHeight){
            height = "100%";
        }
        else{
            height = "auto";
        }
        style.setProperty(CssProperties.BACKGROUND_SIZE, width + " " + height);
    }
}
