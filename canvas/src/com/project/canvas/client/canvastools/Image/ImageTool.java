package com.project.canvas.client.canvastools.Image;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.project.canvas.client.canvastools.base.CanvasTool;
import com.project.canvas.client.canvastools.base.CanvasToolCommon;
import com.project.canvas.client.resources.CanvasResources;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.shared.data.ElementData;
import com.project.canvas.shared.data.ImageData;

public class ImageTool extends FlowPanel implements CanvasTool<ImageData> {
	
	private final SimpleEvent<String> killRequestEvent = new SimpleEvent<String>();
	private ImageData data = new ImageData();
	
	public ImageTool() {
		CanvasToolCommon.initCanvasToolWidget(this);
		this.addStyleName(CanvasResources.INSTANCE.main().imageBox());
		this.registerHandlers();
	}

	private void registerHandlers() {
		this.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				uploadImage();
				
			}
		}, ClickEvent.getType());
	}

	protected void uploadImage() {
		final DialogBox imageSelectionDialog = new DialogBox(false, true);
		
		FlowPanel dialogContents = new FlowPanel();
		final TextBox urlBox = new TextBox();
		urlBox.setWidth("25em");
		dialogContents.add(urlBox);
		Button setUrlButton = new Button("Done");
		setUrlButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				setImageUrl(urlBox.getText());
				imageSelectionDialog.hide();
			}
		});
		dialogContents.add(setUrlButton);
		imageSelectionDialog.add(dialogContents);
		imageSelectionDialog.setGlassEnabled(true);
		imageSelectionDialog.setText("Select image to upload");
		imageSelectionDialog.show();
		imageSelectionDialog.center();
	}

	public void setFocus(boolean isFocused) {
		// do nothing.
	}

	public SimpleEvent<String> getKillRequestedEvent() {
		return this.killRequestEvent;
	}


	protected void setImageUrl(String url) {
		this.getElement().getStyle().setBackgroundImage("url:(\"" + url + "\")");
	}

	@Override
	public ImageData getValue() {
		String imageCss = this.getElement().getStyle().getBackgroundImage();
		this.data.url = imageCss.substring("url:(".length(), imageCss.length()-1);
		// TODO: update size & rotation
		return this.data;
	}

	@Override
	public void setValue(ImageData data) {
		this.data = data;
		// TODO: update size & rotation
		this.setImageUrl(this.data.url);
	}

	@Override
	public void setElementData(ElementData data) {
		this.setValue((ImageData) data);
	}
}
