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
import com.kiouri.sliderbar.client.event.BarValueChangedEvent;
import com.kiouri.sliderbar.client.event.BarValueChangedHandler;
import com.kiouri.sliderbar.client.solution.simplehorizontal.SliderBarSimpleHorizontal;
import com.project.shared.client.events.SimpleEvent;
import com.project.website.canvas.client.shared.widgets.ColorPicker;
import com.project.website.canvas.client.shared.widgets.ToggleButtonPanel;
import com.project.website.canvas.shared.data.SketchOptions;

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

    private final SimpleEvent<SketchOptions> optionsChangedEvent = new SimpleEvent<SketchOptions>();
    private final HashMap<ToggleButton, DrawingTool> buttonToolMap = new HashMap<ToggleButton, DrawingTool>();
    private final SliderBarSimpleHorizontal penWidthSlider = new SliderBarSimpleHorizontal(100, "100px", true);

    private boolean _initialized = false;

    private SketchOptions _sketchOptions = new SketchOptions();

    public SketchToolbar()
    {
        initWidget(uiBinder.createAndBindUi(this));

        buttonToolMap.put(this.eraseButton, DrawingTool.ERASE);
        buttonToolMap.put(this.paintButton, DrawingTool.PAINT);
        buttonToolMap.put(this.spiroButton, DrawingTool.SPIRO);

        this.colorPanel.add(penWidthSlider);

        this.penWidthSlider.addBarValueChangedHandler(new BarValueChangedHandler() {
            @Override public void onBarValueChanged(BarValueChangedEvent event) {
                dispatchOptionsChangedEvent();
            }
        });

        this.color.addChangeHandler(new ChangeHandler() {
            @Override public void onChange(ChangeEvent event) {
                dispatchOptionsChangedEvent();
            }
        });
        this.eraseButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override public void onValueChange(ValueChangeEvent<Boolean> event) {
                dispatchOptionsChangedEvent();
            }
        });
        this.paintButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override public void onValueChange(ValueChangeEvent<Boolean> event) {
                dispatchOptionsChangedEvent();
            }
        });
        this.spiroButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override public void onValueChange(ValueChangeEvent<Boolean> event) {
                dispatchOptionsChangedEvent();
            }
        });

        this.paintButton.setValue(true, true);
    }

    protected void dispatchOptionsChangedEvent()
    {
        this.optionsChangedEvent.dispatch(this.getOptions());
    }

    public SketchOptions getOptions()
    {
        ToggleButton activeButton = toolTogglePanel.getActiveButton();
        if (null == activeButton) {
            // todo dispatch indicating no tool active?
            //return;
        }
        this._sketchOptions.drawingTool = this.buttonToolMap.get(activeButton);
        this._sketchOptions.penWidth = this.penWidthSlider.getValue();
        this._sketchOptions.penColor = this.color.getColor();
        return new SketchOptions(this._sketchOptions);
    }

    public void setOptions(SketchOptions options)
    {
        this._sketchOptions = new SketchOptions(options);
        switch (this._sketchOptions.drawingTool) {
        case ERASE:
            this.eraseButton.setValue(true, false);
            break;
        case PAINT:
            this.paintButton.setValue(true, false);
            break;
        case SPIRO:
            this.spiroButton.setValue(true, false);
            break;
        }
        this.color.setColor(this._sketchOptions.penColor);
        this.penWidthSlider.setValue(this._sketchOptions.penWidth);
    }

    @Override
    protected void onLoad()
    {
        super.onLoad();
        if (false == this._initialized) {
            this._initialized = true;
            this.setOptions(this._sketchOptions);
        }
    }

    @Override
    protected void onUnload()
    {
        super.onUnload();
    }

    public HandlerRegistration addOptionsChangedHandler(SimpleEvent.Handler<SketchOptions> handler) {
        return this.optionsChangedEvent.addHandler(handler);
    }
}
