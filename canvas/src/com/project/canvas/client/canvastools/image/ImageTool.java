package com.project.canvas.client.canvastools.image;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.project.canvas.client.canvastools.base.CanvasTool;
import com.project.canvas.client.canvastools.base.CanvasToolCommon;
import com.project.canvas.client.resources.CanvasResources;
import com.project.canvas.client.shared.RegistrationsManager;
import com.project.canvas.client.shared.WidgetUtils;
import com.project.canvas.client.shared.dialogs.SelectImageDialog;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.client.shared.events.SimpleEvent.Handler;
import com.project.canvas.client.shared.searchProviders.interfaces.ImageSearchProvider;
import com.project.canvas.client.shared.widgets.DialogWithZIndex;
import com.project.canvas.shared.StringUtils;
import com.project.canvas.shared.UrlUtils;
import com.project.canvas.shared.data.ElementData;
import com.project.canvas.shared.data.ImageData;
import com.project.canvas.shared.data.ImageInformation;
import com.project.canvas.shared.data.Point2D;

public class ImageTool extends FlowPanel implements CanvasTool<ImageData>
{
    private final SimpleEvent<MouseEvent<?>> moveStartEvent = new SimpleEvent<MouseEvent<?>>();
    private final RegistrationsManager registrationsManager = new RegistrationsManager();

    private ImageData data = null;
    private SelectImageDialog selectImageDialog;
    private DialogBox imageSelectionDialog;
	private boolean optionsWidgetInited = false;
	private ArrayList<ImageSearchProvider> searchProviders = new ArrayList<ImageSearchProvider>();
    private boolean viewMode;

	public ImageTool(Collection<ImageSearchProvider> imageSearchProviders)
    {
        CanvasToolCommon.initCanvasToolWidget(this);

        searchProviders.addAll(imageSearchProviders);

        WidgetUtils.disableDrag(this);
        super.addStyleName(CanvasResources.INSTANCE.main().imageBox());
        super.addStyleName(CanvasResources.INSTANCE.main().imageToolEmpty());
    }

    @Override
    public void bind() {
        super.setTitle("Click for image options; Control-click to drag");
        this.setViewMode(viewMode); // do whatever bindings necessary for our mode
    }

    private void reRegisterHandlers() {
        registrationsManager.clear();

        registrationsManager.add(this.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                uploadImage();

            }
        }, ClickEvent.getType()));
        registrationsManager.add(this.addDomHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                if (event.isControlKeyDown()) {
                    moveStartEvent.dispatch(event);
                }
            }
        }, MouseDownEvent.getType()));
    }

    private void uploadImage() {
    	initOptionsWidget();
        selectImageDialog.setValue(data.imageInformation);

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                selectImageDialog.setFocus(true);
                imageSelectionDialog.center();
            }
        });
    }

	private void initOptionsWidget() {
		if (optionsWidgetInited) {
			return;
		}
		this.optionsWidgetInited = true;
		this.imageSelectionDialog = new DialogWithZIndex(false, true);
        imageSelectionDialog.setGlassEnabled(true);
        imageSelectionDialog.setText("Image options");

		this.selectImageDialog = new SelectImageDialog();
        imageSelectionDialog.add(selectImageDialog);

        this.selectImageDialog.setSearchProviders(this.searchProviders);
        selectImageDialog.addCancelEvent(new SimpleEvent.Handler<Void>() {
		    @Override
		    public void onFire(Void arg) {
		        imageSelectionDialog.hide();
		    }
		});
        selectImageDialog.addDoneEvent(new SimpleEvent.Handler<ImageInformation>() {
		    @Override
		    public void onFire(ImageInformation arg) {
		        data.imageInformation = selectImageDialog.getValue();
		        setValue(data, true);
		        imageSelectionDialog.hide();
		    }
		});
	}

    @Override
    public void setActive(boolean isFocused) {
        // do nothing.
    }

    private void setImageUrl(String url, boolean autoSize) {
        if (StringUtils.isWhitespaceOrNull(url)) {
            this.getElement().getStyle().setBackgroundImage("");
            super.addStyleName(CanvasResources.INSTANCE.main().imageToolEmpty());
            super.removeStyleName(CanvasResources.INSTANCE.main().imageToolSet());
            return;
        }
        // Make sure we don't set arbitrary html or invalid urls
        url = UrlUtils.encodeOnce(url);
        if (autoSize || (false == UrlUtils.areEquivalent(url, data.imageInformation.url))) {
            data.imageInformation.url = url;
            final ImageTool that = this;
            WidgetUtils.SetBackgroundImageAsync(this, url,
                    CanvasResources.INSTANCE.imageUnavailable().getURL(), autoSize,
                    CanvasResources.INSTANCE.main().imageLoadingStyle(),
                    new SimpleEvent.Handler<Void>() {
                        @Override
                        public void onFire(Void arg) {
                            that.removeStyleName(CanvasResources.INSTANCE.main().imageToolEmpty());
                            that.addStyleName(CanvasResources.INSTANCE.main().imageToolSet());
                        }}, new SimpleEvent.EmptyHandler<Void>());
        }
    }

    @Override
    public ImageData getValue() {
        return this.data;
    }

    public void setValue(ImageData data, boolean autoSize) {
        this.data = data;
        this.setImageUrl(this.data.imageInformation.url, autoSize);
    }

    @Override
    public void setValue(ImageData data) {
        this.setValue(data, false);
    }

    @Override
    public void setElementData(ElementData data) {
        this.setValue((ImageData) data);
    }

    @Override
    public HandlerRegistration addMoveStartEventHandler(Handler<MouseEvent<?>> handler) {
        return this.moveStartEvent.addHandler(handler);
    }

	@Override
	public ResizeMode getResizeMode() {
		return ResizeMode.BOTH;
	}


    @Override
    public boolean canRotate() {
        return true;
    }

    @Override
	public HandlerRegistration addSelfMoveRequestEventHandler(Handler<Point2D> handler) {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public void setViewMode(boolean isViewMode)
    {
        this.viewMode = isViewMode;
        if (isViewMode) {
            registrationsManager.clear();
        }
        else {
            reRegisterHandlers();
        }
    }

    @Override
    public HandlerRegistration addKillRequestEventHandler(Handler<String> handler)
    {
        return null;
    }
}
