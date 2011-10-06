package com.project.website.canvas.client.canvastools.sitecrop;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.client.handlers.MouseButtonDownHandler;
import com.project.shared.client.handlers.RegistrationsManager;
import com.project.shared.client.handlers.SpecificKeyPressHandler;
import com.project.shared.client.utils.ElementUtils;
import com.project.shared.client.utils.SchedulerUtils.OneTimeScheduler;
import com.project.shared.client.utils.UrlUtils;
import com.project.shared.client.utils.WindowUtils;
import com.project.shared.client.utils.widgets.WidgetUtils;
import com.project.shared.data.MouseButtons;
import com.project.shared.data.Point2D;
import com.project.shared.data.Rectangle;
import com.project.website.canvas.client.canvastools.base.CanvasTool;
import com.project.website.canvas.client.canvastools.base.CanvasToolEvents;
import com.project.website.canvas.client.canvastools.base.ICanvasToolEvents;
import com.project.website.canvas.client.canvastools.base.ResizeMode;
import com.project.website.canvas.client.resources.CanvasResources;
import com.project.website.canvas.client.worksheet.ElementDragManagerImpl;
import com.project.website.canvas.client.worksheet.interfaces.ElementDragManager.StopCondition;
import com.project.website.canvas.client.worksheet.interfaces.MouseMoveOperationHandler;
import com.project.website.canvas.shared.data.ElementData;
import com.project.website.canvas.shared.data.SiteCropElementData;

//TODO:
//8. Add "Reset" button

//Chrome Problems:
//Sometimes when dragging the inner frame an exception is thrown due to null mouse position

//IE9 Problems (2):
//Apparently in IE9 they've changed the way IFrames are rendered so now they are rendered using the same engine as
//the parent page. so if the parent page defines a doctype of HTML5, the child page will also be renderd in the same engine.
//which causes problems in some sites (e.g.: ynet.co.il) since they are not supposed to work with that rendering engine.
//http://www.sitepoint.com/forums/showthread.php?743000-IE9-Iframes-DOCTYPES-and-You/page3

public class SiteCropTool extends Composite implements CanvasTool<SiteCropElementData>{

    //#region UiBinder Declarations

    private static SiteCropToolUiBinder uiBinder = GWT.create(SiteCropToolUiBinder.class);

    interface SiteCropToolUiBinder extends UiBinder<Widget, SiteCropTool> {
    }

    //#endregion

    //#region UiFields

    @UiField
    FocusPanel rootPanel;

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

    //#endregion

    private static final int MOUSE_SCROLL_PIXELS = 5;

    private CanvasToolEvents _toolEvents = new CanvasToolEvents(this);

    private SiteCropElementData _data = null;

    private final SimpleEvent<Void> _stopMouseOperationEvent = new SimpleEvent<Void>();
    private ElementDragManagerImpl _frameDragManager = null;
    private SiteFrameSelectionManager _frameSelectionManager = null;

    private RegistrationsManager _registrationManager = new RegistrationsManager();
    private RegistrationsManager _modeRegistrations = new RegistrationsManager();
    private RegistrationsManager _loadedRegistrations = new RegistrationsManager();

    private RegistrationsManager _moveRegistrationManager = new RegistrationsManager();
    private RegistrationsManager _cropRegistrationManager = new RegistrationsManager();

    private Rectangle _minimalRectangle = Rectangle.empty;

    private boolean _isViewMode = false;
    private boolean _isActive = false;
    private boolean _isLoaded = false;

    //TODO: Make singleton and update according to data when displayed.
    private final SiteCropToolbar _toolbar = new SiteCropToolbar();

    private final ScheduledCommand _refreshUrlCommand = new ScheduledCommand() {
        @Override
        public void execute() {
            loadUrl();
    }};

    public SiteCropTool() {
        initWidget(uiBinder.createAndBindUi(this));

        this.initializeFrame();

        this.addStyleName(CanvasResources.INSTANCE.main().cropSiteToolEmpty());

        this.selectionPanel.setVisible(false);
        this.dragPanel.setVisible(false);

        this.initializeToolbar();

        WidgetUtils.disableContextMenu(this.blockPanel);

        this._frameDragManager = new ElementDragManagerImpl(
                this.frameContainer, this.dragPanel, 0, this._stopMouseOperationEvent);
        this._frameSelectionManager = new SiteFrameSelectionManager(
                this.frameContainer, this.dragPanel, this.selectionPanel, this._stopMouseOperationEvent);

        this.setEditMode();
    }

    @Override
    public ICanvasToolEvents getToolEvents()
    {
        return this._toolEvents;
    }

    private void initializeFrame()
    {
        this.siteFrame.getElement().setPropertyString("scrolling", "no");
        this.siteFrame.getElement().setPropertyString("frameborder", "0");
    }

    private void initializeToolbar()
    {
        this._toolbar.enableCrop(false);
        this._toolbar.enableDrag(false);
        this._toolbar.enableBrowse(false);
        this._toolbar.setAcceptCropVisibility(false);
    }

    private void registerGlobalHandlers()
    {
        this._registrationManager.clear();

        this._registrationManager.add(
                this.siteFrame.addLoadHandler(new LoadHandler() {
                    @Override
                    public void onLoad(LoadEvent event) {
                        handleFrameLoaded(event);
                    }
                }));
    }

    private void registerEditModeHandlers()
    {
        this._modeRegistrations.clear();

        this._modeRegistrations.add(this._loadedRegistrations.asSingleRegistration());
        this._modeRegistrations.add(this._cropRegistrationManager.asSingleRegistration());
        this._modeRegistrations.add(this._moveRegistrationManager.asSingleRegistration());

        this._modeRegistrations.add(this.rootPanel.addKeyPressHandler(
                new SpecificKeyPressHandler(KeyCodes.KEY_ESCAPE) {
            @Override
            public void onSpecificKeyPress(KeyPressEvent event) {
                clearSelection();
            }
        }));

        this._modeRegistrations.add(
                this._toolbar.addUrlChangedHandler(new Handler<String>() {
            @Override
            public void onFire(String arg) {
                updateUrl(arg);
            }
        }));

        this._modeRegistrations.add(
                this._toolbar.addBrowseRequestHandler(new Handler<Void>() {
            @Override
            public void onFire(Void arg) {
                WindowUtils.openNewTab(siteFrame.getUrl());
            }
        }));

        this._modeRegistrations.add(
                this._toolbar.addIsInteractiveChangedHandler(new ValueChangeHandler<Boolean>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<Boolean> event) {
                        _data.isInteractive = event.getValue();
                    }
                }));

        if (this._isLoaded)
        {
            this.registerLoadedEditModeHandlers();
        }

//      ONLY FOR DEBUG
        this._modeRegistrations.add(
                this._toolbar.addDebugClickRequestHandler(new Handler<Void>() {
            @Override
            public void onFire(Void arg) {
                updateUrl("http://www.google.com");
            }
        }));
    }

    private void registerViewModeHandlers()
    {
        this._modeRegistrations.clear();
    }

    private void registerLoadedEditModeHandlers()
    {
        this._loadedRegistrations.clear();

        this._loadedRegistrations.add(
                this.addDomHandler(new MouseWheelHandler() {
                    @Override
                    public void onMouseWheel(MouseWheelEvent event) {
                        handleMouseScroll(event);
                    }
                }, MouseWheelEvent.getType()));;

        this._loadedRegistrations.add(
                this._toolbar.addToggleDragRequestHandler(new Handler<Boolean>() {
            @Override
            public void onFire(Boolean arg) {
                if (arg) {
                    enableSiteMove();
                }
                else {
                    disableSiteMove();
                }
            }
        }));

        this._loadedRegistrations.add(
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
        }));
    }

    private void clearSelection()
    {
        _stopMouseOperationEvent.dispatch(null);
        this._frameSelectionManager.clearSelection();
    }

    private void handleFrameLoaded(LoadEvent event)
    {
        if (Strings.isNullOrEmpty(this.siteFrame.getUrl()))
        {
            return;
        }
        this.onLoadEnded();
    }

    private void setDefaultMode()
    {
        this._toolbar.toggleDrag();
        //We don't know if all the handlers are already registered and therefore we can't count on the toolbar
        //to raise the appropriate event which will enable the actual behavior.
        this.enableSiteMove();
    }

    private void cropSelectedFrame()
    {
        Rectangle selectionRect = this._frameSelectionManager.getSelectedRectangle();

        Rectangle frameRect = ElementUtils.getElementOffsetRectangle(this.siteFrame.getElement());
        frameRect = frameRect.move(new Point2D(
                frameRect.getLeft() - selectionRect.getLeft(), frameRect.getTop() - selectionRect.getTop()));
        this.updateFrameDimensions(frameRect);

        this._toolEvents.dispatchSelfMoveRequestEvent(new Point2D(
                selectionRect.getLeft(), selectionRect.getTop()));
        ElementUtils.setElementSize(this.getElement(), selectionRect.getSize());

        this.setMinimalRectangle(frameRect);

        this.clearSelection();
        this.setDefaultMode();
    }

    private void enableSiteMove()
    {
        this._moveRegistrationManager.clear();

        this._moveRegistrationManager.add(
                this.blockPanel.addDomHandler(new MouseButtonDownHandler(MouseButtons.Left) {
                    @Override
                    public void onMouseButtonDown(MouseDownEvent event) {
                        MouseMoveOperationHandler handler = new MouseMoveOperationHandler() {

                            private Rectangle initialFrameRectangle = null;
                            private Point2D lastMousePos = null;

                            @Override public void onStop(Point2D pos) { }
                            @Override public void onStart() {
                                initialFrameRectangle = ElementUtils.getElementOffsetRectangle(siteFrame.getElement());
                                lastMousePos = Point2D.zero;
                            }
                            @Override public void onCancel() {
                                updateFrameDimensions(initialFrameRectangle);
                            }

                            @Override
                            public void onMouseMove(Point2D pos)
                            {
                                Point2D delta = pos.minus(lastMousePos);
                                lastMousePos = pos;
                                updateFrameDimensions(moveFrame(
                                        ElementUtils.getElementOffsetRectangle(siteFrame.getElement()), delta));
                            }

                        };

                        _frameDragManager.startMouseMoveOperation(blockPanel.getElement(),
                                ElementUtils.getRelativePosition(event, blockPanel.getElement()),
                                handler, StopCondition.STOP_CONDITION_MOVEMENT_STOP);
                    }
                }, MouseDownEvent.getType()));
    }

    private void handleMouseScroll(MouseWheelEvent event)
    {
        this.clearSelection();
        Rectangle frameRect = ElementUtils.getElementOffsetRectangle(siteFrame.getElement());
        this.updateFrameDimensions(this.moveFrame(frameRect, new Point2D(0, event.getDeltaY() * -(MOUSE_SCROLL_PIXELS))));
    }

    private Rectangle moveFrame(Rectangle frameRectangle, Point2D delta)
    {
        return new Rectangle(
                Math.min(0, frameRectangle.getLeft() + delta.getX()),
                Math.min(0, frameRectangle.getTop() + delta.getY()),
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
            this.blockPanel.addDomHandler(new MouseButtonDownHandler(MouseButtons.Left) {
                @Override
                public void onMouseButtonDown(MouseDownEvent event) {
                    _toolbar.setAcceptCropVisibility(false);
                    _frameSelectionManager.startSelectionDrag(event, new Handler<Void>() {
                        @Override
                        public void onFire(Void arg) {
                            _toolbar.setAcceptCropVisibility(true);
                        }
                    });
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
        this.clearSelection();
        this._toolbar.setAcceptCropVisibility(false);
    }

    private void setMinimalRectangle(Rectangle rectangle)
    {
        this._minimalRectangle = new Rectangle(rectangle);
    }

    private void updateFrameDimensions(Rectangle rectangle)
    {
        this._data.frameRectangle = new Rectangle(rectangle);

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

    private void updateUrl(String url) {
        if (false == this._data.url.equalsIgnoreCase(url))
        {
            this.resetFramePosition();
            this._data.url = UrlUtils.ensureProtocol(url);
        }
        this.loadUrl();
    }

    private void loadUrl()
    {
        if (false == this.isValidUrl(this._data.url))
        {
            return;
        }

        this.onLoadStarted();

        this.siteFrame.setUrl(this._data.url);
        ElementUtils.setElementSize(this.getElement(),
                ElementUtils.getElementOffsetSize(this.getElement()));
        this.removeStyleName(CanvasResources.INSTANCE.main().cropSiteToolEmpty());
        this.addStyleName(CanvasResources.INSTANCE.main().cropSiteToolSet());
        this._toolbar.setUrl(this._data.url);
        this._toolbar.enableBrowse(true);
    }

    private void onLoadStarted()
    {
        this._isLoaded = false;

        this._toolEvents.dispatchLoadStartedEvent();
        this._toolbar.enableCrop(false);
        this._toolbar.enableDrag(false);

        this._loadedRegistrations.clear();
    }

    private void onLoadEnded()
    {
        this._isLoaded = true;

        this._toolEvents.dispatchLoadEndedEvent();

        this._toolbar.enableCrop(true);
        this._toolbar.enableDrag(true);

        if (this._isActive)
        {
            this.reRegisterModeHandlers();
        }
    }

    private void resetFramePosition()
    {
        this.updateFrameDimensions(ElementUtils.getElementOffsetRectangle(
                this.siteFrame.getElement()).move(new Point2D(0, 0)));
        this.setMinimalRectangle(Rectangle.empty);

        this.onResize();
    }

    private boolean isValidUrl(String url)
    {
        if (Strings.isNullOrEmpty(url))
        {
            return false;
        }
        return true;
    }

    @Override
    public void setValue(SiteCropElementData value) {
        //Currently, after saving the worksheet, all the tools are updated with the saved element data.
        //so we want to avoid unnecessary load of the url in case nothing was actually changed.
        if ((null != this._data) && (this._data.url.equalsIgnoreCase(value.url)))
        {
            this._data = value;
        }
        else
        {
            this._data = value;
            this.loadUrl();
        }

        this.setMinimalRectangle(this._data.frameRectangle);
        this.setToolbarData(value);

        this.setFrameParameters();
    }

    private void setToolbarData(SiteCropElementData data)
    {
        this._toolbar.setIsInteractive(data.isInteractive);
    }

    @Override
    public SiteCropElementData getValue() {
        return this._data;
    }

    @Override
    public void setElementData(ElementData data) {
        this.setValue((SiteCropElementData)data);
    }

    @Override
    public void setActive(boolean isActive) {
        if (isActive == this._isActive)
        {
            return;
        }
        this._isActive = isActive;
        if (false == isActive)
        {
            this._modeRegistrations.clear();
            return;
        }
        this.reRegisterModeHandlers();
    }

    private void reRegisterModeHandlers()
    {
        if (this._isViewMode)
        {
            this.registerViewModeHandlers();
        }
        else
        {
            this.registerEditModeHandlers();
            this.setDefaultMode();
        }
    }

    @Override
    public void bind() {
        this.registerGlobalHandlers();
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
        if (isViewMode) {
            this.setViewMode();
        }
        else {
            this.setEditMode();
        }
    }

    //Don't register any new event handlers here since we only register them in the setActive method so that all the
    //specific handlers (edit mode/view mode) will get called only when the tool is active.
    private void setViewMode()
    {
        this._isViewMode = true;
        this._modeRegistrations.clear();

        this.clearSelection();

        this.setFrameInteractive(this._data.isInteractive);
    }

    //Don't register any new event handlers here since we only register them in the setActive method so that all the
    //specific handlers (edit mode/view mode) will get called only when the tool is active.
    private void setEditMode()
    {
        this._isViewMode = false;
        this._modeRegistrations.clear();

        this.setFrameInteractive(false);

        //Must use Scheduler since it appears that the setEditMode is called during an event and
        //for some reason nothing happens when settings the url of the iframe during that event.
        OneTimeScheduler.get().scheduleDeferredOnce(this._refreshUrlCommand);
    }

    private void setFrameInteractive(Boolean isInteractive)
    {
        blockPanel.setVisible(isInteractive ? false : true);
    }

    @Override
    public void onResize() {
        //Only resize the frame if the tool is larger than the minimal rectangle which is set when cropping the frame.
        //this is done to prevent unwanted movement in the frame during resize due to the fact that after crop the frame
        //size might be bigger than the actual tool.

        Rectangle frameRect = ElementUtils.getElementOffsetRectangle(this.siteFrame.getElement());
        Rectangle toolRect = ElementUtils.getElementOffsetRectangle(this.getElement());

        if (toolRect.getSize().getX() >= this._minimalRectangle.getRight())
        {
            frameRect.setRight(toolRect.getRight());
        }
        if (toolRect.getSize().getY() >= this._minimalRectangle.getBottom())
        {
            frameRect.setBottom(toolRect.getBottom());
        }
        this.updateFrameDimensions(frameRect);
    }

    @Override
    public IsWidget getToolbar()
    {
        return this._toolbar;
    }
}
