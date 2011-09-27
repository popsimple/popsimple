package com.project.website.canvas.client.canvastools.sitecrop;

import com.google.gwt.core.client.GWT;
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
import com.google.gwt.user.client.Window;
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
import com.project.shared.client.utils.WindowUtils;
import com.project.shared.client.utils.widgets.WidgetUtils;
import com.project.shared.data.Point2D;
import com.project.shared.data.Rectangle;
import com.project.shared.utils.RectangleUtils;
import com.project.website.canvas.client.canvastools.base.CanvasTool;
import com.project.website.canvas.client.resources.CanvasResources;
import com.project.website.canvas.client.worksheet.ElementDragManagerImpl;
import com.project.website.canvas.client.worksheet.interfaces.ElementDragManager.StopCondition;
import com.project.website.canvas.client.worksheet.interfaces.MouseMoveOperationHandler;
import com.project.website.canvas.shared.data.ElementData;

public class SiteCropTool extends Composite implements CanvasTool<ElementData>{

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
                        @Override public void onStop(Point2D pos) { }
                        @Override public void onStart() { }
                        @Override public void onCancel() { }

                        @Override
                        public void onMouseMove(Point2D pos)
                        {
                            Point2D deltaPoint = pos.minus(_lastPoint);
                            _lastPoint = pos;
                            updateFrameLeft(deltaPoint.getX());
                            updateFrameTop(deltaPoint.getY());
                        }

                    };

                    _lastPoint = Point2D.zero;
                    _frameDragManager.startMouseMoveOperation(blockPanel.getElement(),
                            ElementUtils.getRelativePosition(event, blockPanel.getElement()),
                            handler, StopCondition.STOP_CONDITION_MOUSE_UP);

                }
            }, MouseDownEvent.getType()));
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

    private void setUrl(String url) {
        siteFrame.setUrl(url);
        this.siteFrame.getElement().setPropertyString("scrolling", "no");
        ElementUtils.setElementSize(this.getElement(),
                ElementUtils.getElementOffsetSize(this.getElement()));
        this.removeStyleName(CanvasResources.INSTANCE.main().cropSiteToolEmpty());
        this.addStyleName(CanvasResources.INSTANCE.main().cropSiteToolSet());
        this._toolbar.setUrl(url);
        this._toolbar.enableBrowse(true);
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
