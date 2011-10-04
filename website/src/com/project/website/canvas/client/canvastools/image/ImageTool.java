package com.project.website.canvas.client.canvastools.image;

import java.util.ArrayList;
import java.util.Collection;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.handlers.RegistrationsManager;
import com.project.shared.client.utils.HandlerUtils;
import com.project.shared.client.utils.StyleUtils;
import com.project.shared.client.utils.UrlUtils;
import com.project.shared.client.utils.widgets.WidgetUtils;
import com.project.shared.utils.CloneableUtils;
import com.project.shared.utils.StringUtils;
import com.project.website.canvas.client.canvastools.base.CanvasTool;
import com.project.website.canvas.client.canvastools.base.CanvasToolCommon;
import com.project.website.canvas.client.canvastools.base.CanvasToolEvents;
import com.project.website.canvas.client.canvastools.base.ICanvasToolEvents;
import com.project.website.canvas.client.canvastools.base.ResizeMode;
import com.project.website.canvas.client.resources.CanvasResources;
import com.project.website.canvas.client.shared.ImageInformationUtils;
import com.project.website.canvas.client.shared.dialogs.SelectImageDialog;
import com.project.website.canvas.client.shared.searchProviders.interfaces.ImageSearchProvider;
import com.project.website.canvas.client.shared.widgets.DialogWithZIndex;
import com.project.website.canvas.shared.data.ElementData;
import com.project.website.canvas.shared.data.ImageData;
import com.project.website.canvas.shared.data.ImageInformation;

public class ImageTool extends Composite implements CanvasTool<ImageData> {
    interface ImageToolUiBinder extends UiBinder<Widget, ImageTool> {
    }

    private static ImageToolUiBinder uiBinder = GWT.create(ImageToolUiBinder.class);

    @UiField
    Button optionsLabel;

    @UiField
    FlowPanel optionsBar;

    private CanvasToolEvents _toolEvents = new CanvasToolEvents(this);
    private final RegistrationsManager registrationsManager = new RegistrationsManager();
    private final RegistrationsManager editModeRegistrationsManager = new RegistrationsManager();

    private ImageData data = null;
    private SelectImageDialog selectImageDialog;
    private DialogBox dialogContainer;
    private boolean optionsWidgetInited = false;
    private ArrayList<ImageSearchProvider> searchProviders = new ArrayList<ImageSearchProvider>();
    private boolean viewMode;

    public ImageTool(Collection<ImageSearchProvider> imageSearchProviders) {
        initWidget(uiBinder.createAndBindUi(this));
        CanvasToolCommon.initCanvasToolWidget(this);

        searchProviders.addAll(imageSearchProviders);

        WidgetUtils.stopClickPropagation(this.optionsLabel);

        this.addStyleName(CanvasResources.INSTANCE.main().imageToolDefault());
        this.addStyleName(CanvasResources.INSTANCE.main().imageToolEmpty());
    }

    @Override
    public ICanvasToolEvents getToolEvents() {
        return this._toolEvents;
    }

    @Override
    public void bind() {
        this.registerHandlers();
        this.setViewMode(viewMode); // do whatever bindings necessary for our
                                    // mode
    }

    private void registerHandlers() {
        registrationsManager.clear();
        registrationsManager.add(this.addDomHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                _toolEvents.dispatchMoveStartRequestEvent(event);
            }
        }, MouseDownEvent.getType()));
    }

    private void setEditModeRegistrations() {
        editModeRegistrationsManager.clear();
        editModeRegistrationsManager.add(this.optionsLabel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                uploadImage();
            }
        }));
        editModeRegistrationsManager.add(this.addDomHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                uploadImage();
            }
        }, DoubleClickEvent.getType()));
    }

    private void uploadImage() {
        initOptionsWidget();
        selectImageDialog.setValue((ImageInformation) CloneableUtils.clone(data.imageInformation));

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

    private void setImageInformation(ImageInformation imageInformation) {
        if (Objects.equal(data.imageInformation, imageInformation)) {
            return;
        }
        // Make sure we don't set arbitrary html or invalid urls
        imageInformation.url = UrlUtils.encodeOnce(imageInformation.url);
        this.data.imageInformation = imageInformation;
        this.updateImageFromData(imageInformation.options.useOriginalSize);
    }

    @Override
    public void setActive(boolean isFocused) {
        // do nothing.
    }

    private void updateImageFromData(boolean autoSize) {
        this.refreshVisibility();
        if (StringUtils.isWhitespaceOrNull(this.data.imageInformation.url)) {
            this.addStyleName(CanvasResources.INSTANCE.main().imageToolEmpty());
            this.removeStyleName(CanvasResources.INSTANCE.main().imageToolSet());
            return;
        }
        this.removeStyleName(CanvasResources.INSTANCE.main().imageToolEmpty());
        StyleUtils.clearBackground(this.getElement().getStyle());

        if (Strings.isNullOrEmpty(this.data.imageInformation.url)) {
            return;
        }

        this._toolEvents.dispatchLoadStartedEvent();

        WidgetUtils.setBackgroundImageAsync(this, this.data.imageInformation.url, CanvasResources.INSTANCE
                .imageUnavailable().getSafeUri().asString(), autoSize,
                CanvasResources.INSTANCE.main().imageToolLoading(),
                new SimpleEvent.Handler<Void>() {
            @Override
            public void onFire(Void arg) {
                setLoadedStyle();
            }
        }, HandlerUtils.<Void> emptyHandler());
    }

    private void setLoadedStyle() {
        ImageInformationUtils.setBackgroundStyle(this, this.data.imageInformation);

        this._toolEvents.dispatchLoadEndedEvent();
        this.addStyleName(CanvasResources.INSTANCE.main().imageToolSet());
    }

    @Override
    public ImageData getValue() {
        return this.data;
    }

    @Override
    public void setValue(ImageData data) {
        this.data = data;
        this.updateImageFromData(false);
    }

    @Override
    public void setElementData(ElementData data) {
        this.setValue((ImageData) data);
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
    public void setViewMode(boolean isViewMode) {
        this.viewMode = isViewMode;
        this.refreshVisibility();
        if (isViewMode) {
            editModeRegistrationsManager.clear();
        } else {
            this.setVisible(true);
            setEditModeRegistrations();
        }
    }

    private void refreshVisibility() {
        if ((this.viewMode) && (StringUtils.isWhitespaceOrNull(this.data.imageInformation.url))) {
            this.setVisible(false);
        } else {
            this.setVisible(true);
        }
    }

    @Override
    public void onResize() {
    }

    @Override
    public IsWidget getToolbar() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean dimOnLoad() {
        return false;
    }
}
