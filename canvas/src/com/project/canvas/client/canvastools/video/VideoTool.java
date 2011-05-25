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
import com.project.canvas.client.canvastools.base.CanvasTool;
import com.project.canvas.client.canvastools.base.CanvasToolCommon;
import com.project.canvas.client.canvastools.media.MediaToolOptions;
import com.project.canvas.client.resources.CanvasResources;
import com.project.canvas.client.shared.RegistrationsManager;
import com.project.canvas.client.shared.WidgetUtils;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.client.shared.events.SimpleEvent.Handler;
import com.project.canvas.client.shared.searchProviders.interfaces.VideoSearchProvider;
import com.project.canvas.client.shared.widgets.DialogWithZIndex;
import com.project.canvas.shared.data.ElementData;
import com.project.canvas.shared.data.MediaData;
import com.project.canvas.shared.data.Point2D;

public class VideoTool extends FlowPanel implements CanvasTool<MediaData> {

    private static final Point2D DEFAULT_SIZE = new Point2D(425, 349);
	private final SimpleEvent<String> killRequestEvent = new SimpleEvent<String>();
    private final SimpleEvent<MouseEvent<?>> moveStartEvent = new SimpleEvent<MouseEvent<?>>();

    private MediaData data = null;
    private final Frame videoFrame = new Frame();
    private MediaToolOptions mediaToolOptionsWidget;
    private DialogBox videoSelectionDialog;
	private boolean optionsWidgetInited = false;
	private ArrayList<VideoSearchProvider> searchProviders = new ArrayList<VideoSearchProvider>();  
	private final RegistrationsManager registrationsManager = new RegistrationsManager();
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
        
        this.add(videoFrame);
    }
    
    @Override
    public void bind() {
        super.setTitle("Click for video options; Shift-click to drag");
        this.setViewMode(viewMode); // do whatever bindings necessary for our mode
    }

    private void reRegisterHandlers() {
        registrationsManager.add(this.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                uploadVideo();

            }
        }, ClickEvent.getType()));
        registrationsManager.add(this.addDomHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                if (event.isShiftKeyDown()) {
                    moveStartEvent.dispatch(event);
                }
            }
        }, MouseDownEvent.getType()));
    }

    protected void uploadVideo() {
    	initOptionsWidget();
    	mediaToolOptionsWidget.setValue(data);

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                mediaToolOptionsWidget.setFocus(true);
                videoSelectionDialog.center();
            }
        });
    }

	private void initOptionsWidget() {
		if (optionsWidgetInited) {
			return;
		}
		this.optionsWidgetInited = true;
		this.videoSelectionDialog = new DialogWithZIndex(false, true);
//		videoSelectionDialog.setAnimationEnabled(true);
        videoSelectionDialog.setGlassEnabled(true);
        videoSelectionDialog.setText("Video options");
        
		this.mediaToolOptionsWidget = new MediaToolOptions();
        videoSelectionDialog.add(mediaToolOptionsWidget);

        this.mediaToolOptionsWidget.setSearchProviders(this.searchProviders);
        mediaToolOptionsWidget.getCancelEvent().addHandler(new SimpleEvent.Handler<Void>() {
		    @Override
		    public void onFire(Void arg) {
		        videoSelectionDialog.hide();
		    }
		});
		mediaToolOptionsWidget.getDoneEvent().addHandler(new SimpleEvent.Handler<Void>() {
		    @Override
		    public void onFire(Void arg) {
		        setValue(mediaToolOptionsWidget.getValue(), true);
		        videoSelectionDialog.hide();
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

    protected void setVideoUrl(String url, boolean autoSize) {
        if (null == url || url.trim().isEmpty()) {
            super.addStyleName(CanvasResources.INSTANCE.main().videoToolEmpty());
            super.removeStyleName(CanvasResources.INSTANCE.main().videoToolSet());
            return;
        }
        if (autoSize) {
            final RegistrationsManager regs = new RegistrationsManager();
            final VideoTool that = this;
            regs.add(this.videoFrame.addLoadHandler(new LoadHandler() {
                @Override
                public void onLoad(LoadEvent event) {
                    WidgetUtils.setWidgetSize(that, new Point2D(videoFrame.getOffsetWidth(), videoFrame.getOffsetHeight()));
                    videoFrame.setWidth("");
                    videoFrame.setHeight("");
                    regs.clear();
                }
            }));
            WidgetUtils.setWidgetSize(this, DEFAULT_SIZE);
        }
        videoFrame.setUrl(fixEmbeddedUrl(url));
        videoFrame.setVisible(true);
        
        super.removeStyleName(CanvasResources.INSTANCE.main().videoToolEmpty());
        super.addStyleName(CanvasResources.INSTANCE.main().videoToolSet());
    }
    
    //TODO: Build the url properly when returning from the media picker.
    private String fixEmbeddedUrl(String url)
    {
        //NOTE: Change the url to the new embed method.
        String newUrl = url.replaceAll("/v/", "/embed/");

        //NOTE: according to: 
        //NOTE: http://www.electrictoolbox.com/float-div-youtube-iframe/
        if (newUrl.indexOf("&wmode=transparent") == -1)
        {
            return newUrl.concat("&wmode=transparent");
        }
        return newUrl;
    }

    @Override
    public MediaData getValue() {
        // TIP: use this page to check java regex: http://www.regexplanet.com/simple/index.html
        this.data.url = this.videoFrame.getUrl().trim().replaceAll("^(url\\(\\\"?)(.*?)(\\\"?\\))$", "$2");
        return this.data;
    }

    public void setValue(MediaData data, boolean autoSize) {
        this.data = data;
        this.setVideoUrl(this.data.url, autoSize);
    }

    @Override
    public void setValue(MediaData data) {
        this.setValue(data, false);
    }

    @Override
    public void setElementData(ElementData data) {
        this.setValue((MediaData) data);
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
}
