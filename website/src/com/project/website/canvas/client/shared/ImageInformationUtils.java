package com.project.website.canvas.client.shared;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.utils.StyleUtils;
import com.project.website.canvas.shared.data.ImageInformation;
import com.project.website.canvas.shared.data.ImageOptions;

//TODO: Probably needs to be a different class, it doesn't exactly complies with ...Utils convention.
public class ImageInformationUtils
{
    public static void setBackgroundStyle(Widget widget, ImageInformation imageInformation)
    {
        ImageOptions imageOptions = imageInformation.options;
        Style style = widget.getElement().getStyle();
        StyleUtils.setBackgroundRepeat(style, imageOptions.repeat);
        StyleUtils.setBackgroundStretch(style,
                imageOptions.stretchWidth, imageOptions.stretchHeight);
        if (imageOptions.center)
        {
            StyleUtils.setBackgroundCenter(style);
        }
    }
}
