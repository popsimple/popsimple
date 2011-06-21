package com.project.canvas.client.canvastools.video;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Label;
import com.project.canvas.client.canvastools.base.CanvasTool;
import com.project.canvas.client.canvastools.base.CanvasToolCommon;
import com.project.canvas.client.resources.CanvasResources;
import com.project.canvas.client.shared.ElementUtils;
import com.project.canvas.client.shared.RegistrationsManager;
import com.project.canvas.client.shared.WidgetUtils;
import com.project.canvas.client.shared.dialogs.SelectVideoDialog;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.client.shared.events.SimpleEvent.Handler;
import com.project.canvas.client.shared.searchProviders.interfaces.VideoSearchProvider;
import com.project.canvas.client.shared.widgets.DialogWithZIndex;
import com.project.canvas.shared.CloneableUtils;
import com.project.canvas.shared.StringUtils;
import com.project.canvas.shared.UrlUtils;
import com.project.canvas.shared.data.ElementData;
import com.project.canvas.shared.data.Point2D;
import com.project.canvas.shared.data.VideoData;
import com.project.canvas.shared.data.VideoInformation;

public class VideoTool extends FlowPanel implements CanvasTool<VideoData>
{
    private static final Point2D DEFAULT_SIZE = new Point2D(425, 349);
    private static final String OPTIONS_LABEL_VIDEO_UNSET = "Choose a video...";
    private static final String OPTIONS_LABEL_VIDEO_SET = "Change video...";

    private final SimpleEvent<MouseEvent<?>> moveStartEvent = new SimpleEvent<MouseEvent<?>>();
    private final Frame videoFrame = new Frame();
    private final RegistrationsManager registrationsManager = new RegistrationsManager();
    private final Label optionsLabel = new Label(OPTIONS_LABEL_VIDEO_UNSET);

    private VideoData data = null;
    private SelectVideoDialog selectVideoDialog;
    private DialogBox dialogContainer;
	private boolean optionsWidgetInited = false;
	private ArrayList<VideoSearchProvider> searchProviders = new ArrayList<VideoSearchProvider>();
    private boolean viewMode;

	public VideoTool(Collection<VideoSearchProvider> videoSearchProviders)
    {
        CanvasToolCommon.initCanvasToolWidget(this);

        searchProviders.addAll(videoSearchProviders);

        WidgetUtils.disableDrag(this);
        super.addStyleName(CanvasResources.INSTANCE.main().videoBox());
        super.addStyleName(CanvasResources.INSTANCE.main().videoToolEmpty());
        this.videoFrame.addStyleName(CanvasResources.INSTANCE.main().videoFrame());
        this.videoFrame.setVisible(false);

        this.optionsLabel.addStyleName(CanvasResources.INSTANCE.main().videoOptionsLabel());

        this.add(videoFrame);
        this.add(optionsLabel);
    }

    @Override
    public void bind() {
        super.setTitle("Click for video options; Control-click to drag");
        this.setViewMode(viewMode); // do whatever bindings necessary for our mode
    }

    private void reRegisterHandlers() {
        registrationsManager.add(this.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                showOptionsDialog();

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
        registrationsManager.add(this.optionsLabel.addClickHandler(new ClickHandler() {
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
        if (data.videoInformation.equals(videoInformation))
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
    public HandlerRegistration addMoveStartEventHandler(Handler<MouseEvent<?>> handler) {
        return this.moveStartEvent.addHandler(handler);
    }


    @Override
    public boolean canRotate() {
        return false;
    }

    @Override
	public HandlerRegistration addSelfMoveRequestEventHandler(Handler<Point2D> handler) {
		// TODO Auto-generated method stub
		return null;
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
            this.registrationsManager.clear();
        }
        else {
            this.reRegisterHandlers();
        }
    }

    @Override
    public HandlerRegistration addKillRequestEventHandler(Handler<String> handler)
    {
        return null;
    }

    private void setValue(VideoData data, boolean autoSize) {
        this.data = data;
        this.setVideo(autoSize);
    }

    private void setVideo(boolean autoSize) {
        if (StringUtils.isWhitespaceOrNull(this.data.videoInformation.url)) {
            super.addStyleName(CanvasResources.INSTANCE.main().videoToolEmpty());
            super.removeStyleName(CanvasResources.INSTANCE.main().videoToolSet());
            return;
        }
        // Make sure we don't set arbitrary html or invalid urls
        String url = UrlUtils.encodeOnce(this.data.videoInformation.url);
        if (autoSize) {
            prepareAutoSizeHandler();
        }
        // Only set the url if it changed, because there will be a refresh of the iframe
        if (autoSize || (false == UrlUtils.areEquivalent(url, videoFrame.getUrl()))) {
            videoFrame.setUrl(url);
        }
        videoFrame.setVisible(true);
        optionsLabel.setText(OPTIONS_LABEL_VIDEO_SET);

        super.removeStyleName(CanvasResources.INSTANCE.main().videoToolEmpty());
        super.addStyleName(CanvasResources.INSTANCE.main().videoToolSet());
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

}
