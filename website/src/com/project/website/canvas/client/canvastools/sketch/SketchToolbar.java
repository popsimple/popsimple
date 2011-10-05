package com.project.website.canvas.client.canvastools.sketch;

import java.util.HashMap;

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
import com.project.website.canvas.client.shared.widgets.ToggleButtonPanel;

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
    ToggleButton spiroButton;

    @UiField
    ToggleButtonPanel toolTogglePanel;

    @UiField
    HTMLPanel colorPanel;

    private final SimpleEvent<String> colorChangedEvent = new SimpleEvent<String>();
    private final SimpleEvent<DrawingTool> toolChangedEvent = new SimpleEvent<DrawingTool>();
    private final HashMap<ToggleButton, DrawingTool> buttonToolMap = new HashMap<ToggleButton, DrawingTool>();

    private boolean _initialized = false;
    private boolean _erasing;

    public SketchToolbar()
    {
        initWidget(uiBinder.createAndBindUi(this));

        buttonToolMap.put(this.eraseButton, DrawingTool.ERASE);
        buttonToolMap.put(this.paintButton, DrawingTool.PAINT);
        buttonToolMap.put(this.spiroButton, DrawingTool.SPIRO);

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
                dispatchToolChangedEvent();
            }
        });
        this.paintButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override public void onValueChange(ValueChangeEvent<Boolean> event) {
                colorPanel.setVisible(event.getValue());
                dispatchToolChangedEvent();
            }
        });
        this.spiroButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override public void onValueChange(ValueChangeEvent<Boolean> event) {
                colorPanel.setVisible(event.getValue());
                dispatchToolChangedEvent();
            }
        });

        this.paintButton.setValue(true, true);
    }

    protected void dispatchToolChangedEvent()
    {
        ToggleButton activeButton = toolTogglePanel.getActiveButton();
        if (null == activeButton) {
            // todo dispatch indicating no tool active?
            return;
        }
        this.toolChangedEvent.dispatch(this.buttonToolMap.get(activeButton));

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

    public HandlerRegistration addToolChangedHandler(SimpleEvent.Handler<DrawingTool> handler) {
        return this.toolChangedEvent.addHandler(handler);
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
