package com.project.website.canvas.client.canvastools.sitecrop;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.client.handlers.RegistrationsManager;
import com.project.shared.client.handlers.SpecificKeyPressHandler;
import com.project.shared.client.utils.ElementUtils;
import com.project.shared.client.utils.EventUtils;
import com.project.shared.client.utils.widgets.WidgetUtils;
import com.project.shared.data.Point2D;
import com.project.shared.data.Rectangle;
import com.project.shared.utils.RectangleUtils;
import com.project.website.canvas.client.canvastools.base.CanvasTool;
import com.project.website.canvas.client.resources.CanvasResources;
import com.project.website.canvas.client.worksheet.ElementDragManagerImpl;
import com.project.website.canvas.client.worksheet.interfaces.ElementDragManager.StopCondition;
import com.project.website.canvas.shared.data.ElementData;

public class SiteCropTool extends Composite implements CanvasTool<ElementData>{

    private static SiteCropToolUiBinder uiBinder = GWT.create(SiteCropToolUiBinder.class);

    interface SiteCropToolUiBinder extends UiBinder<Widget, SiteCropTool> {
    }

    @UiField
    Frame siteFrame;

    @UiField
    FlowPanel frameContainer;
    @UiField
    HTMLPanel blockPanel;
    @UiField
    HTMLPanel selectionPanel;
    @UiField
    HTMLPanel dragPanel;
    @UiField
    FlowPanel coverPanel;

    private int _frameLeft = 0;
    private int _frameTop = 0;
    private Point2D _lastPoint = Point2D.zero;
    private final SimpleEvent<Void> stopOperationEvent = new SimpleEvent<Void>();
    private ElementDragManagerImpl _frameDragManager = null;
    private SiteFrameSelectionManager _frameSelectionManager = null;

    private RegistrationsManager _moveRegistrationManager = new RegistrationsManager();
    private RegistrationsManager _cropRegistrationManager = new RegistrationsManager();

    private SimpleEvent<Point2D> _selfMoveEvent = new SimpleEvent<Point2D>();
    
    //Why not singleton?
    private final SiteCropToolbar _toolbar = new SiteCropToolbar();

    public SiteCropTool() {
        initWidget(uiBinder.createAndBindUi(this));

        this.addStyleName(CanvasResources.INSTANCE.main().cropSiteToolEmpty());

        this.selectionPanel.setVisible(false);
        this.dragPanel.setVisible(false);

        this.registerHandlers();

        WidgetUtils.disableContextMenu(this.blockPanel);

        this._frameDragManager = new ElementDragManagerImpl(
                this.frameContainer, this.dragPanel, 0, this.stopOperationEvent);
        this._frameSelectionManager = new SiteFrameSelectionManager(
                this.frameContainer, this.dragPanel, this.selectionPanel, this.stopOperationEvent);
    }

    private void cropSelectedFrame()
    {
        Rectangle rect = this._frameSelectionManager.getSelectedRectangle();
        coverPanel.getElement().getStyle().setProperty("clip",
                RectangleUtils.toRect(rect, Unit.PX));
        ElementUtils.setElementRectangle(coverPanel.getElement(),
                ElementUtils.getElementOffsetRectangle(coverPanel.getElement()));

        this._selfMoveEvent.dispatch(new Point2D(rect.getLeft(), rect.getTop()));

        ElementUtils.setElementCSSPosition(this.coverPanel.getElement(),
                Point2D.zero.minus(new Point2D(rect.getLeft(), rect.getTop())));
        ElementUtils.setElementSize(this.getElement(), rect.getSize());

        this._frameSelectionManager.clearSelection();
        this.setDefaultMode();
        
        //TODO: Notify listeners about the resize so they can update (e.g. floating toolbar).
    }

    private void setDefaultMode()
    {
        this._toolbar.getMoveButton().setValue(true, true);
    }

    private void registerHandlers()
    {
    	final TextBox urlTextBox = this._toolbar.getUrlTextBox();
    	urlTextBox.addKeyPressHandler(new SpecificKeyPressHandler(KeyCodes.KEY_ENTER) {
            @Override
            public void onSpecificKeyPress(KeyPressEvent event) {
                setUrl(urlTextBox.getText());
            }
        });

        Event.addNativePreviewHandler(new NativePreviewHandler() {
            @Override public void onPreviewNativeEvent(NativePreviewEvent event) {
                NativeEvent nativeEvent = null == event ? null : event.getNativeEvent();
                if (null == nativeEvent) {
                    return;
                }
                handleNativePreviewEvent(event);
            }});

        this._toolbar.getMoveButton().addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                if (event.getValue())
                {
                    enableSiteMove();
                }
                else
                {
                    disableSiteMove();
                }
            }
        });

        this._toolbar.getCropButton().addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                if (event.getValue())
                {
                    enableSiteCrop();
                }
                else
                {
                    disableSiteCrop();
                }
            }
        });

        this._toolbar.getBrowseButton().addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                enableBrowsing(event.getValue());
            }
            });

        //Can't get src from iframe if not in the same domain.
//        this.siteFrame.addLoadHandler(new LoadHandler() {
//			@Override
//			public void onLoad(LoadEvent event) {
//				//TODO: Extract to a proper method.
//				urlTextBox.setText(siteFrame.getUrl());
//			}});

//      ONLY FOR DEBUG
        this._toolbar.getUrlLabel().addClickHandler(new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
              setUrl("http://www.google.com");
          }
      });
    }
    
    private void enableBrowsing(Boolean enable)
    {
        blockPanel.setVisible(enable ? false : true);
    }

    private void enableSiteMove()
    {
        this._moveRegistrationManager.add(
                this.blockPanel.addDomHandler(new MouseDownHandler() {
                @Override
                public void onMouseDown(MouseDownEvent event) {
                    if (NativeEvent.BUTTON_LEFT != event.getNativeButton())
                    {
                        return;
                    }
                    ElementUtils.setTextSelectionEnabled(blockPanel.getElement(), true);
                    Handler<Point2D> moveHandler = new Handler<Point2D>() {
                        @Override
                        public void onFire(Point2D arg) {
                            Point2D deltaPoint = arg.minus(_lastPoint);
                            _lastPoint = arg;
                            updateFrameLeft(deltaPoint.getX());
                            updateFrameTop(deltaPoint.getY());
                        }
                    };

                    _lastPoint = Point2D.zero;
                    _frameDragManager.startMouseMoveOperation(blockPanel.getElement(),
                            ElementUtils.getRelativePosition(event, blockPanel.getElement()),
                            moveHandler, null, null, StopCondition.STOP_CONDITION_MOUSE_UP);

                }
            }, MouseDownEvent.getType()));
    }

    private void disableSiteMove()
    {
        this._moveRegistrationManager.clear();
    }

    private void enableSiteCrop()
    {
        this._cropRegistrationManager.add(
            this.blockPanel.addDomHandler(new MouseDownHandler() {

                @Override
                public void onMouseDown(MouseDownEvent event) {
                    if (NativeEvent.BUTTON_LEFT != event.getNativeButton())
                    {
                        return;
                    }
                    ElementUtils.setTextSelectionEnabled(blockPanel.getElement(), true);
                    _toolbar.getAcceptCropButton().setVisible(true);
                    _frameSelectionManager.startSelectionDrag(event);
                }
            }, MouseDownEvent.getType()));
        this._cropRegistrationManager.add(
        		_toolbar.getAcceptCropButton().addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        cropSelectedFrame();
                    }
                }));
    }

    private void disableSiteCrop()
    {
        this._cropRegistrationManager.clear();
        this._frameSelectionManager.clearSelection();
        _toolbar.getAcceptCropButton().setVisible(false);
    }

    private void handleNativePreviewEvent(NativePreviewEvent event)
    {
        if (EventUtils.nativePreviewEventTypeEquals(event, KeyDownEvent.getType()))
        {
            // TODO: Use some sort of KeyMapper.
            switch (event.getNativeEvent().getKeyCode()) {
            case KeyCodes.KEY_ESCAPE:
                stopOperationEvent.dispatch(null);
                break;
            default:
                break;
            }
        }
    }

    private void updateFrameTop(int delta)
    {
        this._frameTop += delta;
        Style frameStyle = siteFrame.getElement().getStyle();
        frameStyle.setTop(this._frameTop, Unit.PX);
//        if (this.chkAutoSize.getValue())
//        {
            frameStyle.setHeight(siteFrame.getOffsetHeight() - delta, Unit.PX);
//        }
    }

    private void updateFrameLeft(int delta)
    {
        this._frameLeft += delta;
        Style frameStyle = siteFrame.getElement().getStyle();
        frameStyle.setLeft(this._frameLeft, Unit.PX);
//        if (this.chkAutoSize.getValue())
//        {
            frameStyle.setWidth(siteFrame.getOffsetWidth() - delta, Unit.PX);
//        }
    }

    private void setUrl(String text) {
        siteFrame.setUrl(text);
        this.siteFrame.getElement().setPropertyString("scrolling", "no");
        ElementUtils.setElementSize(this.getElement(),
                ElementUtils.getElementOffsetSize(this.getElement()));
        this.removeStyleName(CanvasResources.INSTANCE.main().cropSiteToolEmpty());
        this.addStyleName(CanvasResources.INSTANCE.main().cropSiteToolSet());
    }

    @Override
    public void setValue(ElementData value) {
        // TODO Auto-generated method stub
    }

    @Override
    public ElementData getValue() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HandlerRegistration addKillRequestEventHandler(Handler<String> handler) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HandlerRegistration addMoveStartEventHandler(Handler<MouseEvent<?>> handler) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HandlerRegistration addSelfMoveRequestEventHandler(Handler<Point2D> handler) {
        return this._selfMoveEvent.addHandler(handler);
    }

    @Override
    public void setElementData(ElementData data) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setActive(boolean isActive) {
        // TODO Auto-generated method stub
    }

    @Override
    public void bind() {
        // TODO Auto-generated method stub
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
        // TODO Auto-generated method stub
    }

    @Override
    public void onResize() {
//        this.siteFrame.getElement().getStyle().setWidth(this.getElement().getOffsetWidth(), Unit.PX);
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler handler)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler handler)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IsWidget getToolbar()
    {
        return this._toolbar;
    }

}
