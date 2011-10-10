package com.project.website.canvas.client.shared.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HumanInputEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.client.handlers.RegistrationsManager;
import com.project.shared.client.utils.ElementUtils;
import com.project.shared.client.utils.EventUtils;
import com.project.shared.client.utils.SchedulerUtils;
import com.project.shared.client.utils.widgets.WidgetUtils;
import com.project.shared.data.Point2D;
import com.project.shared.data.Rectangle;

public class Slider extends Composite implements HasValueChangeHandlers<Double>, HasValue<Double> {

	interface SliderUiBinder extends UiBinder<Widget, Slider> {
	}
    private static final int SCALE_PANEL_CHANGE_DELAY = 10;

	private static final int MORE_LESS_BUTTON_STEPS = 10;

	private static SliderUiBinder uiBinder = GWT.create(SliderUiBinder.class);

	@UiField
	Button lessButton;
	@UiField
	Button moreButton;
	@UiField
	Button dragButton;
	@UiField
	FlowPanel scalePanel;
	@UiField
	TextBox valueText;
	@UiField
	HTMLPanel sliderPanel;

	private double _maxValue = 100;
	private double _minValue = 0;
	private double _value = 0;

	private final RegistrationsManager dragRegs = new RegistrationsManager();
	private final RegistrationsManager scalePanelPressedRegs = new RegistrationsManager();

    private boolean _scalePanelPressed;
    private boolean _isDragging;

    /**
     * Amount of value that is occupied in the scale panel by the width of the drag cutton
     */
    private int _valuesPerDragButtonWidth = 0; // will be initialized after onLoad

	public Slider() {
		initWidget(uiBinder.createAndBindUi(this));
		final Slider that = this;

		this.setShowText(false);

		WidgetUtils.addMovementStartHandler(dragButton, new Handler<HumanInputEvent<?>>() {
			@Override public void onFire(HumanInputEvent<?> arg) {
				startDragging();
			}});

		WidgetUtils.addMovementMoveHandler(dragButton, new Handler<HumanInputEvent<?>>() {
			@Override public void onFire(HumanInputEvent<?> arg) {
				double oldValue = that.getValue();
				ValueChangeEvent.fireIfNotEqual(that, oldValue, that.getValue());
			}});

		this.lessButton.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {
				that.setValue(that.getValue() - that.getRange() / MORE_LESS_BUTTON_STEPS);
			}});
		this.moreButton.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {
				that.setValue(that.getValue() + that.getRange() / MORE_LESS_BUTTON_STEPS);
			}});
		this.scalePanel.addDomHandler(new MouseOutHandler() {
            @Override public void onMouseOut(MouseOutEvent event) {
                stopScalePanelDrag();
            }
        }, MouseOutEvent.getType());
        WidgetUtils.addMovementStartHandler(this.scalePanel, new Handler<HumanInputEvent<?>>() {
            @Override public void onFire(HumanInputEvent<?> arg)  {
                if (false == that._isDragging) {
                    handleScalePanelPressed();
                }
            }});
        WidgetUtils.addMovementStopHandler(this.scalePanel, new Handler<HumanInputEvent<?>>() {
            @Override public void onFire(HumanInputEvent<?> arg)  {
                stopScalePanelDrag();
            }});
        WidgetUtils.addMovementMoveHandler(this.scalePanel, new Handler<HumanInputEvent<?>>() {
            @Override public void onFire(HumanInputEvent<?> arg)  {
                if ((that._scalePanelPressed) && (false == that._isDragging)) {
                    handleScalePanelPressed();
                }
            }});

        this.valueText.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override public void onValueChange(ValueChangeEvent<String> event) {
                that.setValue(Double.valueOf(event.getValue()), true, true, false);
            }
        });
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Double> handler) {
		return this.addHandler(handler, ValueChangeEvent.getType());
	}

    public double getMaxValue() {
		return _maxValue;
	}

	public double getMinValue() {
		return _minValue;
	}

	@Override
	public Double getValue() {
		return this._value;
	}

	public boolean isShowText()
    {
        return this.valueText.isVisible();
    }

	public void setMaxValue(double maxValue) {
		this._maxValue = maxValue;
	}

	public void setMinValue(double minValue) {
		this._minValue = minValue;
	}

	public void setShowText(boolean showText)
    {
	    this.valueText.setVisible(showText);
    }

	public void setSliderWidth(String width)
	{
	    this.sliderPanel.setWidth(width);
	}

	@Override
	public void setValue(Double value) {
		this.setValue(value, true);
	}

	@Override
	public void setValue(Double value, boolean fireEvents) {
	    this.setValue(value, fireEvents, true, true);
	}

	private void setValue(Double value, boolean fireEvents, boolean updateDragPosition, boolean updateText) {
        value = Math.min(this._maxValue, Math.max(this._minValue, value));
        Double oldValue = this._value;
        this._value = value;
        if (updateDragPosition) {
            double newDragX = (this._value - this._minValue) / this.getRange() * this.getMaxAllowedDragPosX();
            Point2D pos = new Point2D((int)Math.round(newDragX), 0);
            ElementUtils.setElementCSSPosition(this.dragButton.getElement(), pos);
        }
        if (updateText) {
            this.valueText.setText(value.toString());
        }
        if (fireEvents) {
            ValueChangeEvent.fireIfNotEqual(this, oldValue, value);
        }
	}

	@Override
    protected void onLoad()
    {
        super.onLoad();
        final int scalePanelWidth = this.scalePanel.getOffsetWidth();
        final int dragButtonWidth = this.dragButton.getOffsetWidth();
        this._valuesPerDragButtonWidth = Math.max(1, (int) Math.round((this.getRange() / scalePanelWidth) * dragButtonWidth));
    }

	private void changeOnScalePanelPress(final Point2D pos)
    {
        Rectangle dragButtonRect = ElementUtils.getElementAbsoluteRectangle(this.dragButton.getElement());
        if (dragButtonRect.contains(pos)) {
            return;
        }
        // we must use topLeft corner to handle working on the panel when it is rotated
        Point2D scalePanelTopLeft = ElementUtils.getElementAbsoluteRectangle(this.scalePanel.getElement()).getCorners().topLeft;
        Point2D dragButtonTopLeft = dragButtonRect.getCorners().topLeft;
        boolean isIncrease = 0 < (pos.minus(scalePanelTopLeft).getRadius() - dragButtonTopLeft.minus(scalePanelTopLeft).getRadius());
        int direction = isIncrease ? 1 : -1;
        setValue(getValue() + direction * this._valuesPerDragButtonWidth);
    }

	private Point2D getDragPositionRelativeToScalePanel() {
		return ElementUtils.getElementAbsolutePosition(this.dragButton.getElement()).minus(ElementUtils.getElementAbsolutePosition(this.scalePanel.getElement()));
	}

	private int getMaxAllowedDragPosX() {
		return this.scalePanel.getOffsetWidth() - this.dragButton.getOffsetWidth();
	}

	private double getRange() {
		return this._maxValue - this._minValue;
	}

    private void handleScalePanelPressed()
    {
        stopScalePanelDrag();
        _scalePanelPressed = true;
        final Point2D pos = EventUtils.getCurrentMousePos();
        changeOnScalePanelPress(pos);
        this.scalePanelPressedRegs.add(SchedulerUtils.scheduleFixedPeriod(new RepeatingCommand() {
            @Override public boolean execute() {
                changeOnScalePanelPress(pos);
                return _scalePanelPressed;
            }
        }, SCALE_PANEL_CHANGE_DELAY));
    }

    private void startDragging() {
	    this._isDragging = true;
		this.dragRegs.add(Event.addNativePreviewHandler(new NativePreviewHandler() {
			@Override public void onPreviewNativeEvent(NativePreviewEvent event) {
				if (EventUtils.nativePreviewEventTypeIsAny(event, MouseMoveEvent.getType(), TouchMoveEvent.getType()))
				{
					updateDragPosition(new Point2D(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY()));
					return;
				}
				if (EventUtils.nativePreviewEventTypeIsAny(event, MouseUpEvent.getType(), TouchEndEvent.getType()))
				{
				    _isDragging = false;
					dragRegs.clear();
					return;
				}
			}
		}));
	}

    private void stopScalePanelDrag()
    {
        _scalePanelPressed = false;
        this.scalePanelPressedRegs.clear();
    }

    private void updateDragPosition(Point2D mousePos) {
		Point2D pos = mousePos.minus(ElementUtils.getElementAbsolutePosition(this.scalePanel.getElement()));
		Point2D maxPos = new Point2D(this.getMaxAllowedDragPosX(), 0);
		pos = Point2D.min(maxPos, Point2D.max(Point2D.zero, pos));
		ElementUtils.setElementCSSPosition(this.dragButton.getElement(), pos);
		updateValueFromDragPosition();
	}

    private void updateValueFromDragPosition() {
		double range = getRange();
		double maxDragOffsetX = Math.max(1, ElementUtils.getElementOffsetSize(this.scalePanel.getElement()).getX());
		double normalizedValue = getDragPositionRelativeToScalePanel().getX() / maxDragOffsetX;
		this.setValue((normalizedValue * range) + this._minValue, true, false, true);
	}

}
