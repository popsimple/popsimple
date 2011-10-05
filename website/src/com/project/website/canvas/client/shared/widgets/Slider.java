package com.project.website.canvas.client.shared.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.HumanInputEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
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
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.client.handlers.RegistrationsManager;
import com.project.shared.client.utils.ElementUtils;
import com.project.shared.client.utils.EventUtils;
import com.project.shared.client.utils.widgets.WidgetUtils;
import com.project.shared.data.Point2D;
import com.project.shared.utils.loggers.Logger;

public class Slider extends Composite implements HasValueChangeHandlers<Double>, HasValue<Double> {

	interface SliderUiBinder extends UiBinder<Widget, Slider> {
	}

	private static SliderUiBinder uiBinder = GWT.create(SliderUiBinder.class);

	@UiField
	Button lessButton;
	@UiField
	Button moreButton;
	@UiField
	Button dragButton;
	@UiField
	DivElement scalePanel;
	
	private double _maxValue = 100;
	private double _minValue = 0;
	private double _value = 0;
	
	private final RegistrationsManager dragRegs = new RegistrationsManager();
	
	public Slider() {
		initWidget(uiBinder.createAndBindUi(this));
		final Slider that = this;
		WidgetUtils.addMovementStartHandler(dragButton, new Handler<HumanInputEvent<?>>() {
			@Override public void onFire(HumanInputEvent<?> arg) {
				Logger.info(that, "start");
				startDragging();
			}});
		
		WidgetUtils.addMovementMoveHandler(dragButton, new Handler<HumanInputEvent<?>>() {
			@Override public void onFire(HumanInputEvent<?> arg) {
				Logger.info(that, "move");
				double oldValue = that._value;
				ValueChangeEvent.fireIfNotEqual(that, oldValue, that.getValue());
			}});
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
		this._value = getDragPositionRelativeToScalePanel().getX() / Math.max(1, (this._maxValue - this._minValue));
		return this._value;
	}

	public void setMaxValue(double maxValue) {
		this._maxValue = maxValue;
	}

	public void setMinValue(double minValue) {
		this._minValue = minValue;
	}

	@Override
	public void setValue(Double value) {
		this.setValue(value, true);
	}

	@Override
	public void setValue(Double value, boolean fireEvents) {
		value = Math.min(this._maxValue, Math.max(this._minValue, value));
		Double oldValue = this._value;
		this._value = value;
		if (fireEvents) {
			ValueChangeEvent.fireIfNotEqual(this, oldValue, value);
		}
	}

	protected Point2D getDragPositionRelativeToScalePanel() {
		return ElementUtils.getElementAbsolutePosition(this.dragButton.getElement()).minus(ElementUtils.getElementAbsolutePosition(this.scalePanel));
	}

	protected void startDragging() {
		this.dragRegs.add(Event.addNativePreviewHandler(new NativePreviewHandler() {
			@Override public void onPreviewNativeEvent(NativePreviewEvent event) {
				if (EventUtils.nativePreviewEventTypeIsAny(event, MouseMoveEvent.getType(), TouchMoveEvent.getType())) 
				{
					updateDragPosition();
					return;
				}
				if (EventUtils.nativePreviewEventTypeIsAny(event, MouseUpEvent.getType(), TouchEndEvent.getType())) 
				{
					dragRegs.clear();
					return;
				}
			}
		}));
	}

	protected void updateDragPosition() {
		Point2D pos = ElementUtils.getMousePositionRelativeToElement(this.scalePanel);
		pos = Point2D.min(new Point2D(this.scalePanel.getOffsetWidth(), 0), Point2D.max(Point2D.zero, pos));
		ElementUtils.setElementCSSPosition(this.dragButton.getElement(), pos);
	}
}
