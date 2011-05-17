package com.project.canvas.client.canvastools.Image;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Image;
import com.project.canvas.client.canvastools.base.CanvasTool;
import com.project.canvas.client.canvastools.base.CanvasToolCommon;
import com.project.canvas.client.resources.CanvasResources;
import com.project.canvas.client.shared.DialogWithZIndex;
import com.project.canvas.client.shared.WidgetUtils;
import com.project.canvas.client.shared.ZIndexAllocator;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.shared.data.ElementData;
import com.project.canvas.shared.data.ImageData;

public class ImageTool extends Image implements CanvasTool<ImageData> {
	
	private final SimpleEvent<String> killRequestEvent = new SimpleEvent<String>();
	private ImageData data = new ImageData();
	
	public ImageTool() {
		CanvasToolCommon.initCanvasToolWidget(this);
		WidgetUtils.disableDrag(this);

		super.addStyleName(CanvasResources.INSTANCE.main().imageBox());
		super.addStyleName(CanvasResources.INSTANCE.main().imageToolEmpty());
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
		final DialogBox imageSelectionDialog = new DialogWithZIndex(false, true);
		
		final ImageToolOptions dialogContents = new ImageToolOptions(this.getValue());
		dialogContents.getCancelEvent().addHandler(new SimpleEvent.Handler<Void>() {
			@Override
			public void onFire(Void arg) {
				imageSelectionDialog.hide();
			}
		});
		dialogContents.getDoneEvent().addHandler(new SimpleEvent.Handler<Void>() {
			@Override
			public void onFire(Void arg) {
				setValue(dialogContents.getValue());
				imageSelectionDialog.hide();
			}
		});
	
		imageSelectionDialog.add(dialogContents);
		imageSelectionDialog.setGlassEnabled(true);
		imageSelectionDialog.setText("Image options");
		imageSelectionDialog.center();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				dialogContents.setFocus(true);
				imageSelectionDialog.center();
			}
		});
	}

	@Override
	public void setActive(boolean isFocused) {
		// do nothing.
	}

	public SimpleEvent<String> getKillRequestedEvent() {
		return this.killRequestEvent;
	}


	protected void setImageUrl(String url) {
		this.setUrl(url);
		//		if (null == url || url.trim().isEmpty())
//		{ 
//			super.setUrl("");
//			super.addStyleName(CanvasResources.INSTANCE.main().imageToolEmpty());
//			super.removeStyleName(CanvasResources.INSTANCE.main().imageToolSet());
//			return;
//		}
//		if (url.trim().equals(super.getUrl().trim())) {
//			return;
//		}
//			
//		//super.setUrl(url);
//		this.getElement().setTitle(url);
//		this.getElement().getStyle().setBackgroundImage("url(\"" + url + "\")");
		super.removeStyleName(CanvasResources.INSTANCE.main().imageToolEmpty());
		super.addStyleName(CanvasResources.INSTANCE.main().imageToolSet());
	}

	@Override
	public ImageData getValue() {
//		String imageCss = this.getElement().getStyle().getBackgroundImage();
//		if (imageCss.contains("url(")) {
//			this.data._url = imageCss.substring("url(".length(), imageCss.length()-1);
//		}
//		else {
//			this.data._url = "";
//		}
		this.data._url = this.getUrl();
		//this.data._url = super.getUrl();
		// TODO: update size & rotation
		return this.data;
	}

	@Override
	public void setValue(ImageData data) {
		this.data = data;
		// TODO: update size & rotation
		this.setImageUrl(this.data._url);
	}

	@Override
	public void setElementData(ElementData data) {
		this.setValue((ImageData) data);
	}
}
