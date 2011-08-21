package com.project.website.canvas.client.canvastools.image;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.client.handlers.RegistrationsManager;
import com.project.shared.client.utils.HandlerUtils;
import com.project.shared.client.utils.UrlUtils;
import com.project.shared.client.utils.WidgetUtils;
import com.project.shared.data.Point2D;
import com.project.shared.utils.CloneableUtils;
import com.project.shared.utils.StringUtils;
import com.project.website.canvas.client.canvastools.base.CanvasTool;
import com.project.website.canvas.client.canvastools.base.CanvasToolCommon;
import com.project.website.canvas.client.resources.CanvasResources;
import com.project.website.canvas.client.shared.ImageInformationUtils;
import com.project.website.canvas.client.shared.dialogs.SelectImageDialog;
import com.project.website.canvas.client.shared.searchProviders.interfaces.ImageSearchProvider;
import com.project.website.canvas.client.shared.widgets.DialogWithZIndex;
import com.project.website.canvas.shared.data.ElementData;
import com.project.website.canvas.shared.data.ImageData;
import com.project.website.canvas.shared.data.ImageInformation;

public class ImageTool  extends Composite implements CanvasTool<ImageData>
{
    interface ImageToolUiBinder extends UiBinder<Widget, ImageTool> {}

    private static ImageToolUiBinder uiBinder = GWT.create(ImageToolUiBinder.class);

    @UiField
    Button optionsLabel;

    @UiField
    FlowPanel optionsBar;

    private final SimpleEvent<MouseEvent<?>> moveStartEvent = new SimpleEvent<MouseEvent<?>>();
    private final RegistrationsManager registrationsManager = new RegistrationsManager();

    private ImageData data = null;
    private SelectImageDialog selectImageDialog;
    private DialogBox dialogContainer;
	private boolean optionsWidgetInited = false;
	private ArrayList<ImageSearchProvider> searchProviders = new ArrayList<ImageSearchProvider>();
    private boolean viewMode;



	public ImageTool(Collection<ImageSearchProvider> imageSearchProviders)
	{
	    initWidget(uiBinder.createAndBindUi(this));
        CanvasToolCommon.initCanvasToolWidget(this);

        searchProviders.addAll(imageSearchProviders);

        WidgetUtils.disableDrag(this);
        this.addStyleName(CanvasResources.INSTANCE.main().imageToolDefault());
        this.addStyleName(CanvasResources.INSTANCE.main().imageToolEmpty());
    }

    @Override
    public void bind() {
        this.setViewMode(viewMode); // do whatever bindings necessary for our mode
    }

    private void reRegisterHandlers() {
        registrationsManager.clear();

        registrationsManager.add(this.optionsLabel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                uploadImage();

            }
        }));
        registrationsManager.add(this.addDomHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                moveStartEvent.dispatch(event);
            }
        }, MouseDownEvent.getType()));
    }

    private void uploadImage() {
    	initOptionsWidget();
        selectImageDialog.setValue(
                (ImageInformation)CloneableUtils.clone(data.imageInformation));

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                selectImageDialog.setFocus(true);
                dialogContainer.center();
            }
        });
    }

	private void initOptionsWidget() {
		if (optionsWidgetInited) {
			return;
		}
		this.optionsWidgetInited = true;
		this.dialogContainer = new DialogWithZIndex(false, true);
        dialogContainer.setGlassEnabled(true);
        dialogContainer.setText("Image options");

		this.selectImageDialog = new SelectImageDialog();
        dialogContainer.add(selectImageDialog);

        this.selectImageDialog.setSearchProviders(this.searchProviders);
        this.selectImageDialog.setImageOptionsProvider(new ImageToolOptionsProvider());
        selectImageDialog.addCancelHandler(new SimpleEvent.Handler<Void>() {
		    @Override
		    public void onFire(Void arg) {
		        dialogContainer.hide();
		    }
		});
        selectImageDialog.addDoneHandler(new SimpleEvent.Handler<ImageInformation>() {
		    @Override
		    public void onFire(ImageInformation arg) {
		        setImageInformation(arg);
		        dialogContainer.hide();
		    }
		});
	}

	private void setImageInformation(ImageInformation imageInformation)
	{
	    if (data.imageInformation.equals(imageInformation))
        {
	        return;
        }
	    //Make sure we don't set arbitrary html or invalid urls
	    imageInformation.url = UrlUtils.encodeOnce(imageInformation.url);
	    data.imageInformation = imageInformation;
        setImage(imageInformation.options.useOriginalSize);
	}

    @Override
    public void setActive(boolean isFocused) {
        // do nothing.
    }

    private void setImage(boolean autoSize) {
        if (StringUtils.isWhitespaceOrNull(this.data.imageInformation.url)) {
            this.addStyleName(CanvasResources.INSTANCE.main().imageToolEmpty());
            this.removeStyleName(CanvasResources.INSTANCE.main().imageToolSet());
            return;
        }
        this.removeStyleName(CanvasResources.INSTANCE.main().imageToolEmpty());
        ImageInformationUtils.setWidgetBackgroundAsync(this.data.imageInformation, this, autoSize,
                new SimpleEvent.Handler<Void>() {
                    @Override
                    public void onFire(Void arg) {
                        setLoadedStyle();
                    }}, HandlerUtils.<Void>emptyHandler());
    }

    private void setLoadedStyle()
    {
        this.addStyleName(CanvasResources.INSTANCE.main().imageToolSet());
    }

    @Override
    public ImageData getValue() {
        return this.data;
    }

    @Override
    public void setValue(ImageData data) {
        this.data = data;
        this.setImage(false);
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
            if (StringUtils.isWhitespaceOrNull(this.data.imageInformation.url)) {
                this.setVisible(false);
            }
        }
        else {
            this.setVisible(true);
            reRegisterHandlers();
        }
    }

    @Override
    public HandlerRegistration addKillRequestEventHandler(Handler<String> handler)
    {
        return null;
    }

    @Override
    public void onResize() {
    }
}
