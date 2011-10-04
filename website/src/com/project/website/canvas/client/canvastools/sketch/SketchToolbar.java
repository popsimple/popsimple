package com.project.website.canvas.client.canvastools.sketch;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.events.SimpleEvent;
import com.project.website.canvas.client.shared.widgets.ColorPicker;

public class SketchToolbar extends Composite
{

    private static final String DEFAULT_STROKE_COLOR = "#000000";
    private static final String ERASURE_COLOR = "transparent";

    private static SketchToolbarUiBinder uiBinder = GWT.create(SketchToolbarUiBinder.class);

    interface SketchToolbarUiBinder extends UiBinder<Widget, SketchToolbar>
    {}

    @UiField
    ColorPicker color;

    @UiField
    ToggleButton eraseButton;
    @UiField
    ToggleButton paintButton;

    @UiField
    HTMLPanel colorPanel;

    private final SimpleEvent<String> colorChangedEvent = new SimpleEvent<String>();

    private boolean _initialized = false;
    private boolean _erasing;

    public SketchToolbar()
    {
        initWidget(uiBinder.createAndBindUi(this));

        this.color.addChangeHandler(new ChangeHandler() {
            @Override public void onChange(ChangeEvent event) {
            	setErasing(false);
                dispatchColorChangeEvent();
            }
        });
        this.eraseButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override public void onValueChange(ValueChangeEvent<Boolean> event) {
            	_erasing = event.getValue();
                dispatchColorChangeEvent();
            }
        });
        this.paintButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override public void onValueChange(ValueChangeEvent<Boolean> event) {
				colorPanel.setVisible(event.getValue());
			}
		});

        this.paintButton.setValue(true, true);
    }

    public boolean isErasing()
    {
        return _erasing;
    }

    public void setErasing(boolean isErasing)
    {
        this._erasing = isErasing;
        this.eraseButton.setValue(isErasing);
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
        if (this._erasing) {
            return ERASURE_COLOR;
        }
        return this.color.getColor();
    }

    public void setColor(String color) {
        this.color.setColor(color);
    }



    private void dispatchColorChangeEvent()
    {
        if (_erasing) {
            colorChangedEvent.dispatch(ERASURE_COLOR);
        }
        else {
            colorChangedEvent.dispatch(getColor());
        }
    }

}
