package com.project.shared.client.utils.widgets;

import com.google.gwt.user.client.ui.TextBox;

public class TextBoxUtils
{
    public final static String PROPERTY_PLACEHOLDER = "placeholder";

    /**
     * Sets a placeholder (watermark) text on the textbox which is displayed when
     * the textbox is empty.
     * Supported only in HTML 5.
     *
     * @param textbox the textbox to set the placeholder
     * @param placeholder the text that will be displayed
     */
    public static void setPlaceHolder(TextBox textbox, String placeholder)
    {
        textbox.getElement().setPropertyString(PROPERTY_PLACEHOLDER, placeholder);
    }
}
