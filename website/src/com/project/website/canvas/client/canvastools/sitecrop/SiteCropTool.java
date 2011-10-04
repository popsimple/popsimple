package com.project.website.canvas.client.canvastools.sitecrop;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.client.handlers.MouseButtonDownHandler;
import com.project.shared.client.handlers.RegistrationsManager;
import com.project.shared.client.utils.ElementUtils;
import com.project.shared.client.utils.EventUtils;
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
//
//1. stretch frame when changing the size of the tool after cropping or moving.
//2. set the frame correctly if the page loads again.
//3. handle View/Edit mode correctly.
//6. Disable all toolbar when loading.
//7. Support mouse scroll for movement.

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

    //#endregion

    private CanvasToolEvents _toolEvents = new CanvasToolEvents(this);

    private SiteCropElementData _data = null;

    private final SimpleEvent<Void> _stopMouseOperationEvent = new SimpleEvent<Void>();
    private ElementDragManagerImpl _frameDragManager = null;
    private SiteFrameSelectionManager _frameSelectionManager = null;

    private RegistrationsManager _registrationManager = new RegistrationsManager();
    private RegistrationsManager _moveRegistrationManager = new RegistrationsManager();
    private RegistrationsManager _cropRegistrationManager = new RegistrationsManager();

    //TODO: Make singleton and update according to data when displayed.
    private final SiteCropToolbar _toolbar = new SiteCropToolbar();

    public SiteCropTool() {
        initWidget(uiBinder.createAndBindUi(this));

        this.addStyleName(CanvasResources.INSTANCE.main().cropSiteToolEmpty());

        this.selectionPanel.setVisible(false);
        this.dragPanel.setVisible(false);

        this.initializeToolbar();

        WidgetUtils.disableContextMenu(this.blockPanel);

        this._frameDragManager = new ElementDragManagerImpl(
                this.frameContainer, this.dragPanel, 0, this._stopMouseOperationEvent);
        this._frameSelectionManager = new SiteFrameSelectionManager(
                this.frameContainer, this.dragPanel, this.selectionPanel, this._stopMouseOperationEvent);

        this.setDefaultMode();
    }

    @Override
    public ICanvasToolEvents getToolEvents()
    {
        return this._toolEvents;
    }

    private void initializeToolbar()
    {
        this._toolbar.enableCrop(false);
        this._toolbar.enableBrowse(false);
        this._toolbar.setAcceptCropVisibility(false);
    }

    private void registerHandlers()
    {
        this._registrationManager.clear();

        this._registrationManager.add(this._cropRegistrationManager.asSingleRegistration());
        this._registrationManager.add(this._moveRegistrationManager.asSingleRegistration());

        this._registrationManager.add(EventUtils.addNativePreviewEvent(KeyDownEvent.getType(),
                new Handler<NativePreviewEvent>(){
                    @Override
                    public void onFire(NativePreviewEvent arg) {
                        handlePreviewKeyDownEvent(arg);
                    }}));

        this._registrationManager.add(
                this._toolbar.addUrlChangedHandler(new Handler<String>() {
            @Override
            public void onFire(String arg) {
                setUrl(arg);
            }
        }));

        this._registrationManager.add(
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
        }));

        this._registrationManager.add(
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

        this._registrationManager.add(
                this._toolbar.addBrowseRequestHandler(new Handler<Void>() {
            @Override
            public void onFire(Void arg) {
                WindowUtils.openNewTab(siteFrame.getUrl());
            }
        }));

        this._registrationManager.add(
                this._toolbar.addIsInteractiveChangedHandler(new ValueChangeHandler<Boolean>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<Boolean> event) {
                        _data.isInteractive = event.getValue();
                    }
                }));

        this._registrationManager.add(
            this.siteFrame.addLoadHandler(new LoadHandler() {
                @Override
                public void onLoad(LoadEvent event) {
                    handleFrameLoaded(event);
                }
            }));

//      ONLY FOR DEBUG
        this._toolbar.addDebugClickRequestHandler(new Handler<Void>() {
            @Override
            public void onFire(Void arg) {
                setUrl("http://www.google.com");
            }
        });
    }

    private void handleFrameLoaded(LoadEvent event)
    {
        if (Strings.isNullOrEmpty(this.siteFrame.getUrl()))
        {
            return;
        }
        this._toolEvents.dispatchLoadEndedEvent();
        this._toolbar.enableCrop(true);
    }

    private void handlePreviewKeyDownEvent(NativePreviewEvent event)
    {
        // TODO: Use some sort of KeyMapper.
        switch (event.getNativeEvent().getKeyCode()) {
        case KeyCodes.KEY_ESCAPE:
            _stopMouseOperationEvent.dispatch(null);
            break;
        default:
            break;
        }
    }

    private void setDefaultMode()
    {
        this._toolbar.toggleMoveMode();
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

        this._frameSelectionManager.clearSelection();
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

                        _frameDragManager.startMouseMoveOperation(blockPanel.getElement(),
                                ElementUtils.getRelativePosition(event, blockPanel.getElement()),
                                handler, StopCondition.STOP_CONDITION_MOVEMENT_STOP);
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
            this.blockPanel.addDomHandler(new MouseButtonDownHandler(MouseButtons.Left) {
                @Override
                public void onMouseButtonDown(MouseDownEvent event) {
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
        this._toolbar.setAcceptCropVisibility(false);
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

        this._toolEvents.dispatchLoadStartedEvent();

        this._toolbar.enableCrop(false);

        url = UrlUtils.ensureProtocol(url);

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
        if (Strings.isNullOrEmpty(url))
        {
            return false;
        }
        return true;
    }

    @Override
    public void setValue(SiteCropElementData value) {
        this._data = value;

        this.setToolbarData(value);

        this.loadUrl(this._data.url);

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
    }

    @Override
    public void bind() {
        this.registerHandlers();
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

    private void setViewMode()
    {
      //TODO: Set actual view mode;
//        this._registrationManager.clear();

        this.setFrameInteractive(this._data.isInteractive);
    }

    private void setEditMode()
    {
        //TODO: Set actual edit mode.
//        this.registerHandlers();

        this.setFrameInteractive(false);
    }

    private void setFrameInteractive(Boolean isInteractive)
    {
        blockPanel.setVisible(isInteractive ? false : true);
    }

    @Override
    public void onResize() {
//        Rectangle toolRect = ElementUtils.getElementOffsetRectangle(this.getElement());
//        Rectangle frameRect = ElementUtils.getElementOffsetRectangle(this.siteFrame.getElement());
//
//        frameRect.setRight(toolRect.getRight());
//        frameRect.setBottom(toolRect.getBottom());
//
//        ElementUtils.setElementRectangle(this.siteFrame.getElement(), frameRect);
    }

    @Override
    public IsWidget getToolbar()
    {
        return this._toolbar;
    }

    @Override
    public boolean dimOnLoad() {
        return true;
    }
}
