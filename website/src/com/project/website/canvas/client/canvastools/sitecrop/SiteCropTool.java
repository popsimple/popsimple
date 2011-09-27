package com.project.website.canvas.client.canvastools.sitecrop;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
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
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.client.handlers.RegistrationsManager;
import com.project.shared.client.utils.ElementUtils;
import com.project.shared.client.utils.EventUtils;
import com.project.shared.client.utils.WindowUtils;
import com.project.shared.client.utils.widgets.WidgetUtils;
import com.project.shared.data.Point2D;
import com.project.shared.data.Rectangle;
import com.project.shared.utils.RectangleUtils;
import com.project.shared.utils.StringUtils;
import com.project.website.canvas.client.canvastools.base.CanvasTool;
import com.project.website.canvas.client.resources.CanvasResources;
import com.project.website.canvas.client.worksheet.ElementDragManagerImpl;
import com.project.website.canvas.client.worksheet.interfaces.ElementDragManager.StopCondition;
import com.project.website.canvas.client.worksheet.interfaces.MouseMoveOperationHandler;
import com.project.website.canvas.shared.data.ElementData;
import com.project.website.canvas.shared.data.SiteCropElementData;

public class SiteCropTool extends Composite implements CanvasTool<SiteCropElementData>{

    //#region UiBinder Declarations

    private static SiteCropToolUiBinder uiBinder = GWT.create(SiteCropToolUiBinder.class);

    interface SiteCropToolUiBinder extends UiBinder<Widget, SiteCropTool> {
    }

    //#endregion

    //#region UiFields

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

    //#endregion

    private SiteCropElementData _data = null;

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

        this.initializeToolbar();

        this.registerHandlers();

        WidgetUtils.disableContextMenu(this.blockPanel);

        this._frameDragManager = new ElementDragManagerImpl(
                this.frameContainer, this.dragPanel, 0, this.stopOperationEvent);
        this._frameSelectionManager = new SiteFrameSelectionManager(
                this.frameContainer, this.dragPanel, this.selectionPanel, this.stopOperationEvent);

        this.setDefaultMode();
    }

    private void initializeToolbar()
    {
        this._toolbar.enableBrowse(false);
        this._toolbar.setAcceptCropVisibility(false);
    }

    private void cropSelectedFrame()
    {
        Rectangle rect = this._frameSelectionManager.getSelectedRectangle();
        rect.copyTo(this._data.clipRectangle);

        Rectangle coverRectangle = ElementUtils.getElementOffsetRectangle(coverPanel.getElement());
        coverRectangle.copyTo(this._data.coverRectangle);

        this.setCropParameters();

        this._selfMoveEvent.dispatch(new Point2D(
                this._data.clipRectangle.getLeft(), this._data.clipRectangle.getTop()));
        ElementUtils.setElementSize(this.getElement(), this._data.clipRectangle.getSize());

        this._frameSelectionManager.clearSelection();
        this.setDefaultMode();
    }

    private void setCropParameters()
    {
        if (this._data.coverRectangle.equals(Rectangle.empty))
        {
            return;
        }

        ElementUtils.setElementRectangle(coverPanel.getElement(), this._data.coverRectangle);

        //TODO: Extract to utils
        coverPanel.getElement().getStyle().setProperty("clip",
                RectangleUtils.toRect(this._data.clipRectangle, Unit.PX));

        ElementUtils.setElementCSSPosition(this.coverPanel.getElement(), Point2D.zero.minus(
                new Point2D(this._data.clipRectangle.getLeft(), this._data.clipRectangle.getTop())));
    }

    private void setDefaultMode()
    {
        this._toolbar.toggleMoveMode();
        //We don't know if all the handlers are already registered and therefor we can't count on the toolbar
        //to raise the appropriate event which will enable the actual behavior.
        this.enableSiteMove();
    }

    private void registerHandlers()
    {
        Event.addNativePreviewHandler(new NativePreviewHandler() {
            @Override public void onPreviewNativeEvent(NativePreviewEvent event) {
                NativeEvent nativeEvent = null == event ? null : event.getNativeEvent();
                if (null == nativeEvent) {
                    return;
                }
                handleNativePreviewEvent(event);
            }});

        this._toolbar.addUrlChangedHandler(new Handler<String>() {
            @Override
            public void onFire(String arg) {
                setUrl(arg);
            }
        });

        this._toolbar.addToggleMoveModeRequestHandler(new Handler<Boolean>() {
            @Override
            public void onFire(Boolean arg) {
                if (arg) {
                    enableSiteMove();
                }
                else {
                    disableSiteMove();
                }
            }
        });

        this._toolbar.addToggleCropModeRequestHandler(new Handler<Boolean>() {

            @Override
            public void onFire(Boolean arg) {
                if (arg) {
                    enableSiteCrop();
                }
                else {
                    disableSiteCrop();
                }
            }
        });

        this._toolbar.addBrowseRequestHandler(new Handler<Void>() {
            @Override
            public void onFire(Void arg) {
                WindowUtils.openNewTab(siteFrame.getUrl());
            }
        });

//      ONLY FOR DEBUG
        this._toolbar.addDebugClickRequestHandler(new Handler<Void>() {
            @Override
            public void onFire(Void arg) {
                setUrl("http://www.google.com");
            }
        });
    }

//    private void enableBrowsing(Boolean enable)
//    {
//        blockPanel.setVisible(enable ? false : true);
//    }

    private void enableSiteMove()
    {
        this._moveRegistrationManager.clear();

        this._moveRegistrationManager.add(
                this.blockPanel.addDomHandler(new MouseDownHandler() {
                @Override
                public void onMouseDown(MouseDownEvent event) {
                    if (NativeEvent.BUTTON_LEFT != event.getNativeButton())
                    {
                        return;
                    }
                    ElementUtils.setTextSelectionEnabled(blockPanel.getElement(), true);

                    MouseMoveOperationHandler handler = new MouseMoveOperationHandler() {

                        private Rectangle initialFrameRectangle = null;

                        @Override public void onStop(Point2D pos) { }
                        @Override public void onStart() {
                            initialFrameRectangle = ElementUtils.getElementOffsetRectangle(siteFrame.getElement());
                        }
                        @Override public void onCancel() {
                            updateFrameDimensions(initialFrameRectangle);
                        }

                        @Override
                        public void onMouseMove(Point2D pos)
                        {
                            updateFrameDimensions(resolveFrameMovement(initialFrameRectangle, pos));
                        }

                    };

                    _lastPoint = Point2D.zero;
                    _frameDragManager.startMouseMoveOperation(blockPanel.getElement(),
                            ElementUtils.getRelativePosition(event, blockPanel.getElement()),
                            handler, StopCondition.STOP_CONDITION_MOUSE_UP);

                }
            }, MouseDownEvent.getType()));
    }

    private Rectangle resolveFrameMovement(Rectangle frameRectangle, Point2D delta)
    {
        return new Rectangle(
                frameRectangle.getLeft() + delta.getX(),
                frameRectangle.getTop() + delta.getY(),
                frameRectangle.getRight(),
                frameRectangle.getBottom());
    }

    private void disableSiteMove()
    {
        this._moveRegistrationManager.clear();
    }

    private void enableSiteCrop()
    {
        this._cropRegistrationManager.clear();
        this._cropRegistrationManager.add(
            this.blockPanel.addDomHandler(new MouseDownHandler() {

                @Override
                public void onMouseDown(MouseDownEvent event) {
                    if (NativeEvent.BUTTON_LEFT != event.getNativeButton())
                    {
                        return;
                    }
                    ElementUtils.setTextSelectionEnabled(blockPanel.getElement(), true);
                    _toolbar.setAcceptCropVisibility(true);
                    _frameSelectionManager.startSelectionDrag(event);
                }
            }, MouseDownEvent.getType()));
        this._cropRegistrationManager.add(
        		_toolbar.addAcceptCropRequestHandler(new Handler<Void>() {
                    @Override
                    public void onFire(Void arg) {
                        cropSelectedFrame();
                    }
                }));
    }

    private void disableSiteCrop()
    {
        this._cropRegistrationManager.clear();
        this._frameSelectionManager.clearSelection();
        _toolbar.setAcceptCropVisibility(false);
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

    private void updateFrameDimensions(Rectangle rectangle)
    {
        rectangle.copyTo(this._data.frameRectangle);

        this.setFrameParameters();
    }

    private void setFrameParameters()
    {
        if (this._data.frameRectangle.equals(Rectangle.empty))
        {
            return;
        }
        ElementUtils.setElementRectangle(this.siteFrame.getElement(), this._data.frameRectangle);
    }

    private void setUrl(String url) {
        this._data.url = url;

        this.loadUrl(url);
    }

    private void loadUrl(String url)
    {
        if (false == this.isValidUrl(url))
        {
            return;
        }
        //TODO: Called twice due to toolbar changes.
        this.siteFrame.setUrl(url);
        this.siteFrame.getElement().setPropertyString("scrolling", "no");
        ElementUtils.setElementSize(this.getElement(),
                ElementUtils.getElementOffsetSize(this.getElement()));
        this.removeStyleName(CanvasResources.INSTANCE.main().cropSiteToolEmpty());
        this.addStyleName(CanvasResources.INSTANCE.main().cropSiteToolSet());
        this._toolbar.setUrl(url);
        this._toolbar.enableBrowse(true);
    }

    private boolean isValidUrl(String url)
    {
        if (StringUtils.isEmptyOrNull(url))
        {
            return false;
        }
        return true;
    }

    @Override
    public void setValue(SiteCropElementData value) {
        this._data = value;

        this.loadUrl(this._data.url);

        this.setFrameParameters();
        this.setCropParameters();
    }

    @Override
    public SiteCropElementData getValue() {
        return this._data;
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
        this.setValue((SiteCropElementData)data);
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
