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
import com.project.shared.utils.StringUtils;
import com.project.website.canvas.client.shared.widgets.ColorPicker;
import com.project.website.canvas.client.shared.widgets.Slider;
import com.project.website.canvas.client.shared.widgets.ToggleButtonPanel;
import com.project.website.canvas.shared.data.SketchOptions;

public class SketchToolbar extends Composite
{
    private static SketchToolbarUiBinder uiBinder = GWT.create(SketchToolbarUiBinder.class);

    interface SketchToolbarUiBinder extends UiBinder<Widget, SketchToolbar>{}

    private static final String DEFAULT_STROKE_COLOR = "#000000";

    @UiField
    ColorPicker paintColor;

    @UiField
    ToggleButton eraseButton;
    @UiField
    ToggleButton paintButton;
    @UiField
    ToggleButton spiroButton;

    @UiField
    ToggleButtonPanel toolTogglePanel;

    @UiField
    HTMLPanel paintOptionsPanel;
    @UiField
    HTMLPanel eraseOptionsPanel;
    @UiField
    HTMLPanel spiroOptionsPanel;

    @UiField
    Slider paintWidthSlider;
    @UiField
    Slider eraseWidthSlider;

    private final SimpleEvent<SketchOptions> optionsChangedEvent = new SimpleEvent<SketchOptions>();
    private final HashMap<ToggleButton, DrawingTool> buttonToolMap = new HashMap<ToggleButton, DrawingTool>();
    
    private boolean _initialized = false;

    private SketchOptions _sketchOptions = new SketchOptions();

    public SketchToolbar()
    {
        initWidget(uiBinder.createAndBindUi(this));
        
        buttonToolMap.put(this.eraseButton, DrawingTool.ERASE);
        buttonToolMap.put(this.paintButton, DrawingTool.PAINT);
        buttonToolMap.put(this.spiroButton, DrawingTool.SPIRO);

        this.paintOptionsPanel.add(paintWidthSlider);
        this.eraseOptionsPanel.add(eraseWidthSlider);

        ValueChangeHandler<Double> barValueChangedHandler = new ValueChangeHandler<Double>() {
			@Override public void onValueChange(ValueChangeEvent<Double> event) {
                handleOptionChanged();
			}};
        
        ValueChangeHandler<Boolean> optionsChangedHandleBool = new ValueChangeHandler<Boolean>() {
            @Override public void onValueChange(ValueChangeEvent<Boolean> event) {
                handleOptionChanged();
            }
        };

        this.paintColor.addChangeHandler(new ChangeHandler() {
            @Override public void onChange(ChangeEvent event) {
                handleOptionChanged();
            }
        });
        
		this.paintWidthSlider.addValueChangeHandler(barValueChangedHandler);
		this.eraseWidthSlider.addValueChangeHandler(barValueChangedHandler);
        
		this.eraseButton.addValueChangeHandler(optionsChangedHandleBool);
        this.paintButton.addValueChangeHandler(optionsChangedHandleBool);
        this.spiroButton.addValueChangeHandler(optionsChangedHandleBool);

        // TODO: replace with set default values?
        //this.paintButton.setValue(true, false);
    }

    protected void handleOptionChanged()
    {
        SketchOptions options = this.getOptions();
        hideAllOptionPanels();
        switch (options.drawingTool) {
        case ERASE:
        	this.eraseOptionsPanel.setVisible(true);
        	break;
        case SPIRO:
        	this.spiroOptionsPanel.setVisible(true);
        	// fall through
        case PAINT:
        	// fall through
    	default:
        	this.paintOptionsPanel.setVisible(true);
        	break;
        }
		this.optionsChangedEvent.dispatch(options);
    }

	protected void hideAllOptionPanels() {
		this.eraseOptionsPanel.setVisible(false);
        this.paintOptionsPanel.setVisible(false);
        this.spiroOptionsPanel.setVisible(false);
	}

    /**
     * Returns a <strong>copy</strong> of the current options used in the toolbar. 
     */
    public SketchOptions getOptions()
    {
        ToggleButton activeButton = toolTogglePanel.getActiveButton();
        if (null == activeButton) {
            // todo dispatch indicating no tool active?
            //return;
        }
        this._sketchOptions.drawingTool = this.buttonToolMap.get(activeButton);
        this._sketchOptions.penWidth = Math.max(1, this.paintWidthSlider.getValue().intValue());
        this._sketchOptions.penColor = this.paintColor.getColor();
        this._sketchOptions.eraserWidth = Math.max(1, this.eraseWidthSlider.getValue().intValue());
        return new SketchOptions(this._sketchOptions);
    }

    /**
     * <strong>Copies</strong> the given option object and updates the toolbar to reflect the set options.
     */
    public void setOptions(SketchOptions options)
    {
        this._sketchOptions = new SketchOptions(options);
        switch (this._sketchOptions.drawingTool) {
        case ERASE:
            this.eraseButton.setValue(true, false);
            break;
        case SPIRO:
            this.spiroButton.setValue(true, false);
            break;
        case PAINT:
        	// fall through
        default:
            this.paintButton.setValue(true, false);
            break;
        }
        this.paintColor.setColor(StringUtils.defaultIfNullOrEmpty(this._sketchOptions.penColor, DEFAULT_STROKE_COLOR));
        this.paintWidthSlider.setValue(Double.valueOf(this._sketchOptions.penWidth));
        this.eraseWidthSlider.setValue(Double.valueOf(this._sketchOptions.eraserWidth));
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
