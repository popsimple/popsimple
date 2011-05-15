package com.project.canvas.client.canvastools.Image;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.project.canvas.client.shared.UrlUtils;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.shared.data.ImageData;

public class ImageToolOptions extends Composite implements TakesValue<ImageData> {

	private static ImageToolOptionsUiBinder uiBinder = GWT
			.create(ImageToolOptionsUiBinder.class);

	interface ImageToolOptionsUiBinder extends
			UiBinder<Widget, ImageToolOptions> {
	}
	
	@UiField
	TextBox urlTextBox;
	
	@UiField
	Button doneButton;
	
	@UiField
	Button cancelButton;

	private ImageData data;

	private SimpleEvent<Void> doneEvent = new SimpleEvent<Void>();

	private SimpleEvent<Void> cancelEvent  = new SimpleEvent<Void>();
	
	public ImageToolOptions(ImageData value) {
		initWidget(uiBinder.createAndBindUi(this));
		this.setValue(value);
		this.doneButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (UrlUtils.isValidUrl(urlTextBox.getText(), false)) {
					doneEvent.dispatch(null);
				}
				else {
					Window.alert("Invalid url.");
				}
			}
		});
		this.cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				cancelEvent.dispatch(null);
			}
		});
	}

	SimpleEvent<Void> getDoneEvent() {
		return this.doneEvent;
	}
	SimpleEvent<Void> getCancelEvent() {
		return this.cancelEvent;
	}

	@Override
	public void setValue(ImageData value) {
		this.data = value;
		this.urlTextBox.setText(value.url);
	}

	@Override
	public ImageData getValue() {
		this.data.url = this.urlTextBox.getText();
		return this.data;
	}
}
