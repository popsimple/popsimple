package com.project.shared.client.utils.widgets;

import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.user.client.ui.ListBox;

public class ListBoxUtils
{
    public static OptionElement getOptionElement(ListBox listBox, int index)
    {
        SelectElement element = SelectElement.as(listBox.getElement());
        return element.getOptions().getItem(index);
    }
}
