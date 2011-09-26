package com.project.website.canvas.client.shared.widgets;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.TextBox;
import com.project.website.canvas.client.resources.CanvasResources;

public class ColorPicker extends TextBox
{
    public ColorPicker()
    {
        /* color class is used by jscolor.js */
        this.addStyleName(CanvasResources.INSTANCE.main().colorPickerButton());
        this.addStyleName("color {pickerClosable:true}");

        this.addClickHandler(new ClickHandler() {
            @Override public void onClick(ClickEvent event) {
                setPickerVisible(true);
            }
        });

    }


    @Override
    protected void onLoad()
    {
        super.onLoad();
        this.bindJsColor(this.getElement());
    }

    private final native void bindJsColor(Element elem)
    /*-{
         if (elem.color) {
             return;
         }
         var c = $wnd.jscolor.color;
         var myPicker = new c(elem, {pickerClosable:true});
         elem.color = myPicker;
    }-*/;

    public void setPickerVisible(boolean isVisible)
    {
        this.setPickerVisible(this.getElement(), isVisible);
    }


    private final native void setPickerVisible(Element elem, boolean isVisible)
    /*-{
        if (!elem.color) {
            return;
        }
        if (isVisible) {
            elem.color.showPicker();
        }
        else {
            elem.color.hidePicker();
        }
    }-*/;


    public String getColor()
    {
        return this.getElement().getStyle().getBackgroundColor();
    }

    private final native int getColor(Element elem, int index) /*-{
        return elem.color.rgb[index];
    }-*/;
}
