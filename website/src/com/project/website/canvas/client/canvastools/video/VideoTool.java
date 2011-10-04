package com.project.website.canvas.client.canvastools.video;

import java.util.ArrayList;
import java.util.Collection;

import com.google.common.base.Objects;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.handlers.RegistrationsManager;
import com.project.shared.client.utils.ElementUtils;
import com.project.shared.client.utils.UrlUtils;
import com.project.shared.client.utils.widgets.WidgetUtils;
import com.project.shared.data.Point2D;
import com.project.shared.utils.CloneableUtils;
import com.project.shared.utils.StringUtils;
import com.project.website.canvas.client.canvastools.base.CanvasTool;
import com.project.website.canvas.client.canvastools.base.CanvasToolCommon;
import com.project.website.canvas.client.canvastools.base.CanvasToolEvents;
import com.project.website.canvas.client.canvastools.base.ICanvasToolEvents;
import com.project.website.canvas.client.canvastools.base.ResizeMode;
import com.project.website.canvas.client.resources.CanvasResources;
import com.project.website.canvas.client.shared.dialogs.SelectVideoDialog;
import com.project.website.canvas.client.shared.searchProviders.interfaces.VideoSearchProvider;
import com.project.website.canvas.client.shared.widgets.DialogWithZIndex;
import com.project.website.canvas.shared.data.ElementData;
import com.project.website.canvas.shared.data.VideoData;
import com.project.website.canvas.shared.data.VideoInformation;

public class VideoTool extends Composite implements CanvasTool<VideoData>
{
    private static VideoToolUiBinder uiBinder = GWT.create(VideoToolUiBinder.class);

    interface VideoToolUiBinder extends UiBinder<Widget, VideoTool>{ }

    @UiField
    HTMLPanel rootPanel;

    @UiField
    Button optionsLabel;

    @UiField
    FlowPanel optionsBar;

    @UiField
    Frame videoFrame;

    private static final Point2D DEFAULT_SIZE = new Point2D(425, 349);
    private static final String OPTIONS_LABEL_VIDEO_SET = "Change video...";

    private CanvasToolEvents _toolEvents = new CanvasToolEvents(this);

    private final RegistrationsManager registrationsManager = new RegistrationsManager();
    private final RegistrationsManager _editModeRegistrations = new RegistrationsManager();

    private VideoData data = null;
    private SelectVideoDialog selectVideoDialog;
    private DialogBox dialogContainer;
    private boolean optionsWidgetInited = false;
    private ArrayList<VideoSearchProvider> searchProviders = new ArrayList<VideoSearchProvider>();
    private boolean viewMode;

    public VideoTool(Collection<VideoSearchProvider> videoSearchProviders)
    {
        initWidget(uiBinder.createAndBindUi(this));
        CanvasToolCommon.initCanvasToolWidget(this);

        this.registerGeneralHandlers();

        searchProviders.addAll(videoSearchProviders);

        WidgetUtils.mouseDownPreventDefault(this);
        super.addStyleName(CanvasResources.INSTANCE.main().videoToolEmpty());
        //this.videoFrame.setVisible(false);
    }

    @Override
    public ICanvasToolEvents getToolEvents()
    {
        return this._toolEvents;
    }

    @Override
    public void bind() {
        super.setTitle("Control-click to drag");
        this.setViewMode(viewMode); // do whatever bindings necessary for our mode
    }

    private void registerGeneralHandlers() {
        registrationsManager.add(this.videoFrame.addLoadHandler(new LoadHandler() {
            @Override
            public void onLoad(LoadEvent event) {
                _toolEvents.dispatchLoadEndedEvent();
            }
        }));
    }

    private void registerEditModeHandlers()
    {
        this._editModeRegistrations.add(this.rootPanel.addDomHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                if (event.isControlKeyDown()) {
                    _toolEvents.dispatchMoveStartRequestEvent(event);
                }
            }
        }, MouseDownEvent.getType()));
        this._editModeRegistrations.add(this.optionsLabel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                showOptionsDialog();
            }
        }));
    }

    private void showOptionsDialog() {
        initOptionsWidget();
        this.selectVideoDialog.setValue(
                (VideoInformation)CloneableUtils.clone(data.videoInformation));

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                selectVideoDialog.setFocus(true);
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
        dialogContainer.setText("Video options");

        this.selectVideoDialog = new SelectVideoDialog();
        dialogContainer.add(this.selectVideoDialog);

        this.selectVideoDialog.setSearchProviders(this.searchProviders);
        this.selectVideoDialog.addCancelHandler(new SimpleEvent.Handler<Void>() {
            @Override
            public void onFire(Void arg) {
                dialogContainer.hide();
            }
        });
        this.selectVideoDialog.addDoneHandler(new SimpleEvent.Handler<VideoInformation>() {
            @Override
            public void onFire(VideoInformation arg) {
                setVideoInformation(arg);
                dialogContainer.hide();
            }
        });
    }

    private void setVideoInformation(VideoInformation videoInformation)
    {
        if (Objects.equal(data.videoInformation, videoInformation))
        {
            return;
        }
        //Make sure we don't set arbitrary html or invalid urls
        videoInformation.url = UrlUtils.encodeOnce(videoInformation.url);
        data.videoInformation = videoInformation;
        setVideo(true);
    }

    @Override
    public void setActive(boolean isFocused) {
        // do nothing.
    }

    @Override
    public VideoData getValue() {
        return this.data;
    }

    @Override
    public void setValue(VideoData data) {
        this.setValue(data, false);
    }

    @Override
    public void setElementData(ElementData data) {
        this.setValue((VideoData) data);
    }

    @Override
    public boolean canRotate() {
        return true;
    }

    @Override
    public ResizeMode getResizeMode() {
        return ResizeMode.BOTH;
    }

    @Override
    public void setViewMode(boolean isViewMode)
    {
        this.viewMode = isViewMode;
        if (isViewMode) {
            this._editModeRegistrations.clear();
            if (StringUtils.isWhitespaceOrNull(this.data.videoInformation.url)) {
                this.setVisible(false);
            }
            this._editModeRegistrations.clear();
        }
        else {
            this.registerEditModeHandlers();
        }
    }

    private void setValue(VideoData data, boolean autoSize) {
        this.data = data;
        this.setVideo(autoSize);
    }

    private void setVideo(boolean autoSize) {
        if (StringUtils.isWhitespaceOrNull(this.data.videoInformation.url)) {
            this.clearData();
            return;
        }
        // Make sure we don't set arbitrary html or invalid urls
        String url = UrlUtils.encodeOnce(this.data.videoInformation.url);
        if (autoSize) {
            prepareAutoSizeHandler();
        }
        // Only set the url if it changed, because there will be a refresh of the iframe
        if (autoSize || (false == UrlUtils.areEquivalent(url, videoFrame.getUrl()))) {
            _toolEvents.dispatchLoadStartedEvent();
            videoFrame.setUrl(url);
        }
        optionsLabel.setText(OPTIONS_LABEL_VIDEO_SET);

        super.removeStyleName(CanvasResources.INSTANCE.main().videoToolEmpty());
        super.addStyleName(CanvasResources.INSTANCE.main().videoToolSet());
    }

    private void clearData()
    {
        super.addStyleName(CanvasResources.INSTANCE.main().videoToolEmpty());
        super.removeStyleName(CanvasResources.INSTANCE.main().videoToolSet());
        this.videoFrame.setUrl("");
    }

    private void prepareAutoSizeHandler() {
        final RegistrationsManager regs = new RegistrationsManager();
        final VideoTool that = this;
        regs.add(this.videoFrame.addLoadHandler(new LoadHandler() {
            @Override
            public void onLoad(LoadEvent event) {
                Point2D videoFrameSize = ElementUtils.getElementOffsetSize(videoFrame.getElement());
                WidgetUtils.setWidgetSize(that, videoFrameSize);
                videoFrame.setWidth("");
                videoFrame.setHeight("");
                regs.clear();
            }
        }));
        Point2D currentFrameSize = ElementUtils.getElementOffsetSize(videoFrame.getElement());
        if ((false == videoFrame.isVisible()) || currentFrameSize.equals(Point2D.zero)) {
            WidgetUtils.setWidgetSize(this, DEFAULT_SIZE);
        }
        else {
            WidgetUtils.setWidgetSize(this, ElementUtils.getElementOffsetSize(videoFrame.getElement()));
        }
    }

    @Override
    public void onResize() {
        // TODO Auto-generated method stub
    }

    @Override
    public IsWidget getToolbar()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean dimOnLoad() {
        return false;
    }
}
