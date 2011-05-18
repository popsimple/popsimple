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
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
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

public class ImageTool extends FlowPanel implements CanvasTool<ImageData> {
	
	private final SimpleEvent<String> killRequestEvent = new SimpleEvent<String>();
	private final SimpleEvent<MouseEvent<?>> moveStartEvent = new SimpleEvent<MouseEvent<?>>();
	
	private ImageData data = new ImageData();
	private final Image image = new Image();
	
	public ImageTool() {
		CanvasToolCommon.initCanvasToolWidget(this);
		WidgetUtils.disableDrag(this);
		super.addStyleName(CanvasResources.INSTANCE.main().imageBox());
		super.addStyleName(CanvasResources.INSTANCE.main().imageToolEmpty());
		this.add(this.image);
		this.image.setVisible(false);
	}

	@Override
	public void bind() {
		super.setTitle("Right-click for image options");
		this.registerHandlers();
	}
	
	private void registerHandlers() {
		this.addDomHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				if (0 != (event.getNativeButton() & Event.BUTTON_RIGHT)) {
					uploadImage();
					event.preventDefault();
				}
				else {
					moveStartEvent.dispatch(event);
				}
			}
		}, MouseDownEvent.getType());
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
			super.addStyleName(CanvasResources.INSTANCE.main().imageToolEmpty());
			super.removeStyleName(CanvasResources.INSTANCE.main().imageToolSet());
			return;
		}
		final RegistrationsManager regs = new RegistrationsManager();
		regs.add(this.image.addLoadHandler(new LoadHandler() {
			@Override
			public void onLoad(LoadEvent event) {
				getElement().getStyle().setWidth(image.getWidth(), Unit.PX);
				getElement().getStyle().setHeight(image.getHeight(), Unit.PX);
				image.setUrl(""); // don't display the image in the <img>, only as background
				image.setVisible(false);
				regs.clear();
			}
		}));
		// Temporarily set auto width/height so that we will be able to find out
		// the size of the image.
		getElement().getStyle().setProperty("width", "auto");
		getElement().getStyle().setProperty("height", "auto");
		Image.prefetch(url);
		image.setUrl(url);
		image.setVisible(true);
		getElement().getStyle().setBackgroundImage("url(\"" + url + "\")");
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
