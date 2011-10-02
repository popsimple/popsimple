package com.project.website.canvas.client.canvastools.sketch;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.events.SimpleEvent;
import com.project.website.canvas.client.shared.widgets.ColorPicker;

public class SketchToolbar extends Composite
{

    private static final String DEFAULT_STROKE_COLOR = "#000000";

    private static SketchToolbarUiBinder uiBinder = GWT.create(SketchToolbarUiBinder.class);

    interface SketchToolbarUiBinder extends UiBinder<Widget, SketchToolbar>
    {}

    @UiField
    ColorPicker color;

    private final SimpleEvent<String> colorChangedEvent = new SimpleEvent<String>();

    private boolean _initialized = false;

    public SketchToolbar()
    {
        initWidget(uiBinder.createAndBindUi(this));

        this.color.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event)
            {
                colorChangedEvent.dispatch(color.getColor());
            }
        });
    }



    @Override
    protected void onLoad()
    {
        super.onLoad();
        if (false == this._initialized) {
            this._initialized = true;
            this.setColor(DEFAULT_STROKE_COLOR);
        }
    }



    @Override
    protected void onUnload()
    {
        // TODO Auto-generated method stub
        super.onUnload();
    }



    public HandlerRegistration addColorChangedHandler(SimpleEvent.Handler<String> handler) {
        return this.colorChangedEvent.addHandler(handler);
    }

    public String getColor() {
        return this.color.getColor();
    }

    public void setColor(String color) {
        this.color.setColor(color);
    }

}
