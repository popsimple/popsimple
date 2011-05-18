package com.project.canvas.client.canvastools.Image;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Image;
import com.project.canvas.client.canvastools.base.CanvasTool;
import com.project.canvas.client.canvastools.base.CanvasToolCommon;
import com.project.canvas.client.resources.CanvasResources;
import com.project.canvas.client.shared.DialogWithZIndex;
import com.project.canvas.client.shared.RegistrationsManager;
import com.project.canvas.client.shared.WidgetUtils;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.client.shared.events.SimpleEvent.Handler;
import com.project.canvas.shared.data.ElementData;
import com.project.canvas.shared.data.ImageData;

public class ImageTool extends Image implements CanvasTool<ImageData> {
	
	private final SimpleEvent<String> killRequestEvent = new SimpleEvent<String>();
	private final SimpleEvent<MouseEvent<?>> moveStartEvent = new SimpleEvent<MouseEvent<?>>();
	
	private ImageData data = new ImageData();
	
	public ImageTool() {
		CanvasToolCommon.initCanvasToolWidget(this);
		WidgetUtils.disableDrag(this);
		super.addStyleName(CanvasResources.INSTANCE.main().imageBox());
		super.addStyleName(CanvasResources.INSTANCE.main().imageToolEmpty());
	}

	@Override
	public void bind() {
		super.setTitle("Click for image options; Shift-click to drag");
		this.registerHandlers();
	}
	
	private void registerHandlers() {
		this.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				uploadImage();
				
			}
		});
		this.addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				if (event.isShiftKeyDown()) {
					moveStartEvent.dispatch(event);
				}
			}
		});
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
		if (null == url || url.trim().isEmpty())
		{ 
			this.getElement().getStyle().setBackgroundImage("");
		}
		else {
			final RegistrationsManager regs = new RegistrationsManager();
			regs.add(this.addLoadHandler(new LoadHandler() {
				@Override
				public void onLoad(LoadEvent event) {
					getElement().getStyle().setWidth(getWidth(), Unit.PX);
					getElement().getStyle().setHeight(getHeight(), Unit.PX);
					setUrl(""); // don't display the image in the <img>, only as background
					regs.clear();
				}
			}));
			// Temporarily set auto width/height so that we will be able to find out
			// the size of the image.
			getElement().getStyle().setProperty("width", "auto");
			getElement().getStyle().setProperty("height", "auto");
			Image.prefetch(url);
			this.setUrl(url);
			getElement().getStyle().setBackgroundImage("url(\"" + url + "\")");
		}
		super.removeStyleName(CanvasResources.INSTANCE.main().imageToolEmpty());
		super.addStyleName(CanvasResources.INSTANCE.main().imageToolSet());
	}

	@Override
	public ImageData getValue() {
		String imageCss = this.getElement().getStyle().getBackgroundImage();
		if (imageCss.contains("url(")) {
			this.data._url = imageCss.substring("url(\"".length(), imageCss.length() - "\")".length());
		}
		else {
			this.data._url = "";
		}
		return this.data;
	}

	@Override
	public void setValue(ImageData data) {
		this.data = data;
		this.setImageUrl(this.data._url);
	}

	@Override
	public void setElementData(ElementData data) {
		this.setValue((ImageData) data);
	}

	@Override
	public HandlerRegistration addMoveStartEventHandler(Handler<MouseEvent<?>> handler) {
		return this.moveStartEvent.addHandler(handler);
	}
}
