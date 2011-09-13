package com.project.website.canvas.client.worksheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.client.handlers.RegistrationsManager;
import com.project.shared.client.utils.ElementUtils;
import com.project.shared.client.utils.HandlerUtils;
import com.project.shared.data.Point2D;
import com.project.shared.utils.CloneableUtils;
import com.project.website.canvas.client.canvastools.CursorToolboxItem;
import com.project.website.canvas.client.canvastools.MoveToolboxItem;
import com.project.website.canvas.client.canvastools.base.CanvasTool;
import com.project.website.canvas.client.canvastools.base.CanvasToolFactory;
import com.project.website.canvas.client.canvastools.base.CanvasToolFrame;
import com.project.website.canvas.client.canvastools.base.ToolboxItem;
import com.project.website.canvas.client.resources.CanvasResources;
import com.project.website.canvas.client.shared.ImageInformationUtils;
import com.project.website.canvas.client.shared.ImageOptionTypes;
import com.project.website.canvas.client.shared.ImageOptionsProviderUtils;
import com.project.website.canvas.client.shared.ZIndexAllocator;
import com.project.website.canvas.client.shared.dialogs.SelectImageDialog;
import com.project.website.canvas.client.shared.searchProviders.SearchProviders;
import com.project.website.canvas.client.shared.widgets.DialogWithZIndex;
import com.project.website.canvas.client.worksheet.interfaces.ElementDragManager;
import com.project.website.canvas.client.worksheet.interfaces.ToolFrameTransformer;
import com.project.website.canvas.client.worksheet.interfaces.WorksheetView;
import com.project.website.canvas.shared.data.CanvasPageOptions;
import com.project.website.canvas.shared.data.ElementData;
import com.project.website.canvas.shared.data.ImageInformation;
import com.project.website.canvas.shared.data.Transform2D;
import com.project.website.shared.data.UserProfile;

public class WorksheetViewImpl extends Composite implements WorksheetView {
    interface WorksheetViewImplUiBinder extends UiBinder<Widget, WorksheetViewImpl> {
    }

    private static WorksheetViewImplUiBinder uiBinder = GWT.create(WorksheetViewImplUiBinder.class);

    @UiField
    HTMLPanel dragPanel;

    @UiField
    Anchor optionsBackground;

    @UiField
    Anchor linkLogout;

    @UiField
    Anchor linkInvite;

    @UiField
    Button saveButton;
    @UiField
    Button viewButton;

    @UiField
    FlowPanel worksheetBackground;
    @UiField
    HTMLPanel worksheetContainer;

    @UiField
    HTMLPanel worksheetHeader;

    @UiField
    FocusPanel focusPanel;

    @UiField
    FlowPanel worksheetPanel;

    @UiField
    HTMLPanel selectionPanel;

    @UiField
    Label userWelcomeLabel;

    @UiField
    Label statusLabel;

    private Handler<Void> _floatingWidgetTerminator;
    private ToolboxItem activeToolboxItem;
    private Widget floatingWidget;
    private CanvasPageOptions pageOptions;
    private WorksheetImageOptionsProvider _imageOptionsProvider = new WorksheetImageOptionsProvider();

    private final ToolFrameTransformer _toolFrameTransformer;
    private final ToolFrameSelectionManager _toolFrameSelectionManager;
    private final ElementDragManagerImpl _floatingWidgetDragManager;

    private final DialogBox optionsDialog = new DialogWithZIndex(false, true);
    private final SelectImageDialog selectImageDialog = new SelectImageDialog();
    private final HashMap<CanvasToolFrame, RegistrationsManager> toolFrameRegistrations = new HashMap<CanvasToolFrame, RegistrationsManager>();

    private final HashSet<CanvasToolFrame> overToolFrames = new HashSet<CanvasToolFrame>();
    private final RegistrationsManager editModeRegistrations = new RegistrationsManager();
    private final RegistrationsManager allModesRegistrations = new RegistrationsManager();

    private final SimpleEvent<CanvasPageOptions> optionsUpdatedEvent = new SimpleEvent<CanvasPageOptions>();
    private final SimpleEvent<Void> stopOperationEvent = new SimpleEvent<Void>();
    private final SimpleEvent<ArrayList<CanvasToolFrame>> removeToolsRequest = new SimpleEvent<ArrayList<CanvasToolFrame>>();
    private final SimpleEvent<ArrayList<CanvasToolFrame>> copyToolsRequest = new SimpleEvent<ArrayList<CanvasToolFrame>>();
    private final SimpleEvent<Void> pasteToolsRequest = new SimpleEvent<Void>();
    private final SimpleEvent<ToolCreationRequest> toolCreationRequestEvent = new SimpleEvent<ToolCreationRequest>();
    private final SimpleEvent<CanvasToolFrame> toolFrameClickEvent = new SimpleEvent<CanvasToolFrame>();

    private HashSet<CanvasToolFrame> selectedTools = new HashSet<CanvasToolFrame>();

    private boolean viewMode;
    private boolean viewModeSet = false;

    public WorksheetViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));

        this.createDefaultPageOptions();

        this._toolFrameTransformer = new ToolFrameTransformerImpl(worksheetPanel, dragPanel,
                stopOperationEvent);
        this.dragPanel.setVisible(false);

        this._toolFrameSelectionManager = new ToolFrameSelectionManager(this, worksheetPanel, dragPanel,
                selectionPanel, stopOperationEvent);
        this.selectionPanel.setVisible(false);

        this._floatingWidgetDragManager = new ElementDragManagerImpl(
                this, this.dragPanel, 0, stopOperationEvent);

        optionsDialog.setText("Worksheet options");
        this.selectImageDialog.setImageOptionsProvider(this._imageOptionsProvider);
        this.selectImageDialog.setSearchProviders(SearchProviders.getDefaultImageSearchProviders());
        optionsDialog.add(this.selectImageDialog);

        this.addRegistrations();
        this.setViewMode(false);
    }

    private void createDefaultPageOptions()
    {
        this.pageOptions = new CanvasPageOptions();
        ImageOptionsProviderUtils.setImageOptions(this._imageOptionsProvider,
                pageOptions.backgroundImage.options, ImageOptionTypes.OriginalSize);
    }

    @Override
    public HandlerRegistration addLoadHandler(final Handler<String> handler) {
        return null;
//        return loadButton.addClickHandler(new ClickHandler() {
//            @Override
//            public void onClick(ClickEvent event) {
//                load(handler);
//            }
//        });
    }

    @Override
    public HandlerRegistration addOptionsUpdatedHandler(Handler<CanvasPageOptions> handler) {
        return optionsUpdatedEvent.addHandler(handler);
    }

    @Override
    public HandlerRegistration addSaveHandler(Handler<Void> handler) {
        return saveButton.addClickHandler(HandlerUtils.asClickHandler(handler));
    }

    @Override
    public HandlerRegistration addLogoutHandler(Handler<Void> handler) {
        return this.linkLogout.addClickHandler(HandlerUtils.asClickHandler(handler));
    }

    @Override
    public HandlerRegistration addInviteHandler(Handler<Void> handler)
    {
        return this.linkInvite.addClickHandler(HandlerUtils.asClickHandler(handler));
    }

    @Override
    public HandlerRegistration addStopOperationHandler(Handler<Void> handler) {
        return stopOperationEvent.addHandler(handler);
    }

    @Override
    public HandlerRegistration addToolCreationRequestHandler(Handler<ToolCreationRequest> handler) {
        return toolCreationRequestEvent.addHandler(handler);
    }

    @Override
    public HandlerRegistration addToolFrameClickHandler(Handler<CanvasToolFrame> handler) {
        return this.toolFrameClickEvent.addHandler(handler);
    }

    @Override
    public HandlerRegistration addRemoveToolsRequest(Handler<ArrayList<CanvasToolFrame>> handler) {
        return this.removeToolsRequest.addHandler(handler);
    }

    @Override
    public void addToolInstanceWidget(final CanvasToolFrame toolFrame, final Transform2D transform,
            final Point2D additionalOffset) {
        RegistrationsManager regs = new RegistrationsManager();
        toolFrameRegistrations.put(toolFrame, regs);

        regs.add(toolFrame.addAttachHandler(new AttachEvent.Handler() {
            @Override
            public void onAttachOrDetach(AttachEvent event) {
                if (event.isAttached()) {
                    setToolFrameTransform(toolFrame, transform, additionalOffset);
                }
            }
        }));

        regs.add(toolFrame.addMoveStartRequestHandler(new SimpleEvent.Handler<MouseEvent<?>>() {
            @Override
            public void onFire(MouseEvent<?> arg) {
                _toolFrameSelectionManager.forceToolFrameSelection(toolFrame);
                _toolFrameTransformer.startDragCanvasToolFrames(selectedTools, arg);
            }
        }));
        regs.add(toolFrame.addResizeStartRequestHandler(new SimpleEvent.Handler<MouseEvent<?>>() {
            @Override
            public void onFire(MouseEvent<?> arg) {
                _toolFrameSelectionManager.forceToolFrameSelection(toolFrame);
                _toolFrameTransformer.startResizeCanvasToolFrame(toolFrame, arg);
            }
        }));
        if (toolFrame.getTool().canRotate()) {
            regs.add(toolFrame.addRotateStartRequestHandler(new SimpleEvent.Handler<MouseEvent<?>>() {
                @Override
                public void onFire(MouseEvent<?> arg) {
                    _toolFrameSelectionManager.forceToolFrameSelection(toolFrame);
                    _toolFrameTransformer.startRotateCanvasToolFrame(toolFrame, arg);
                }
            }));
        }
        regs.add(toolFrame.addFocusHandler(new FocusHandler() {
            @Override
            public void onFocus(FocusEvent event) {
                toolFrameClickEvent.dispatch(toolFrame);
            }
        }));
        regs.add(toolFrame.asWidget().addDomHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                overToolFrames.add(toolFrame);
            }
        }, MouseOverEvent.getType()));
        regs.add(toolFrame.asWidget().addDomHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                overToolFrames.remove(toolFrame);
            }
        }, MouseOutEvent.getType()));
        regs.add(toolFrame.addMouseUpHandler(new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent event) {
                _toolFrameSelectionManager.handleToolFrameSelection(toolFrame);
            }
        }));

        this.worksheetPanel.add(toolFrame);
    }

    public void selectToolFrame(CanvasToolFrame toolFrame) {
        this.selectedTools.add(toolFrame);
        toolFrame.addStyleName(CanvasResources.INSTANCE.main().selected());
    }

    public void unSelectToolFrame(CanvasToolFrame toolFrame) {
        this.selectedTools.remove(toolFrame);
        toolFrame.removeStyleName(CanvasResources.INSTANCE.main().selected());
    }

    public boolean isToolFrameSelected(CanvasToolFrame toolFrame) {
        return this.selectedTools.contains(toolFrame);
    }

    public void clearToolFrameSelection() {
        ArrayList<CanvasToolFrame> framesToClear = new ArrayList<CanvasToolFrame>(this.selectedTools);
        for (CanvasToolFrame toolFrame : framesToClear) {
            this.unSelectToolFrame(toolFrame);
        }
    }

    @Override
    public HandlerRegistration addViewHandler(Handler<Void> handler) {
        return viewButton.addClickHandler(HandlerUtils.asClickHandler(handler));
    }

    @Override
    public void clearActiveToolboxItem() {
        clearFloatingWidget();
        if (null != this.activeToolboxItem) {
            this.worksheetPanel.removeStyleName(this.activeToolboxItem.getCanvasStyleInCreateMode());
            this.activeToolboxItem = null;
        }
    }

    @Override
    public void onLoadOperationChange(OperationStatus status, String reason) {
        switch (status) {
        case PENDING:
            this.statusLabel.setText("Loading...");
            break;
        case SUCCESS:
            this.statusLabel.setText("");
            break;
        case FAILURE:
            this.statusLabel.setText("Failed to load :(");
            break;
        }
        if (OperationStatus.FAILURE == status) {
            Window.alert("Load failed. Reason: " + reason);
        }
    }

    @Override
    public void onSaveOperationChange(OperationStatus status, String reason) {
        this.changeStatusLabel(saveButton, status, "Saving...", "Save");
        if (OperationStatus.FAILURE == status) {
            Window.alert("Save failed. Reason: " + reason);
        }
    }

    @Override
    public void removeToolInstanceWidget(CanvasToolFrame toolFrame) {
        this.worksheetPanel.remove(toolFrame);
        this.overToolFrames.remove(toolFrame);
        this.selectedTools.remove(toolFrame);
        RegistrationsManager regs = toolFrameRegistrations.remove(toolFrame);
        if (null != regs) {
            regs.clear();
        }
    }

    @Override
    public void setActiveToolboxItem(final ToolboxItem toolboxItem) {
        this.clearActiveToolboxItem();
        this.activeToolboxItem = toolboxItem;
        this.worksheetPanel.addStyleName(toolboxItem.getCanvasStyleInCreateMode());



        CanvasToolFactory<? extends CanvasTool<? extends ElementData>> factory = toolboxItem.getToolFactory();
        if (null == factory) {
            return;
        }

        setFloatingWidgetForTool(factory);
        if (null == this.floatingWidget) {
            setActiveToolboxItemWithoutFloatingWidget(toolboxItem);
            return;
        }

        this.startDraggingFloatingWidget(toolboxItem);
    }

    @Override
    public void setOptions(final CanvasPageOptions value) {
        this.pageOptions = value;

        ImageInformationUtils.setWidgetBackgroundAsync(
                value.backgroundImage, this.worksheetBackground, false);
    }

    @Override
    public void setViewMode(boolean isViewMode) {
        if (this.viewModeSet && (this.viewMode == isViewMode)) {
            return;
        }
        this.viewMode = isViewMode;
        this.viewModeSet = true;
        for (CanvasToolFrame frame : toolFrameRegistrations.keySet()) {
            frame.setViewMode(isViewMode);
        }
        if (isViewMode) {
            this.editModeRegistrations.clear();

            worksheetHeader.addStyleName(CanvasResources.INSTANCE.main().displayNone());
            addStyleName(CanvasResources.INSTANCE.main().worksheetFullView());
            addStyleName(CanvasResources.INSTANCE.main().worksheetModeViewOnly());
            removeStyleName(CanvasResources.INSTANCE.main().worksheetModeEditable());

        } else {
            this.addEditModeRegistrations();

            worksheetHeader.removeStyleName(CanvasResources.INSTANCE.main().displayNone());
            removeStyleName(CanvasResources.INSTANCE.main().worksheetFullView());
            removeStyleName(CanvasResources.INSTANCE.main().worksheetModeViewOnly());
            addStyleName(CanvasResources.INSTANCE.main().worksheetModeEditable());
        }
    }

    private void addRegistrations() {
        this.addEditModeRegistrations();

        this.allModesRegistrations.add(this.optionsBackground.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                selectImageDialog.setValue(
                    (ImageInformation)CloneableUtils.clone(pageOptions.backgroundImage));
                optionsDialog.center();
            }
        }));
        this.allModesRegistrations.add(this.selectImageDialog.addCancelHandler(new SimpleEvent.Handler<Void>() {
            @Override
            public void onFire(Void arg) {
                optionsDialog.hide();
            }
        }));
        this.allModesRegistrations.add(this.selectImageDialog.addDoneHandler(new SimpleEvent.Handler<ImageInformation>() {

            @Override
            public void onFire(ImageInformation arg) {
                optionsDialog.hide();
                onBackgroundImageSelected(arg);
            }
        }));
        final WorksheetViewImpl that = this;
        this.allModesRegistrations.add(this.focusPanel.addKeyDownHandler(new KeyDownHandler(){
            @Override
            public void onKeyDown(KeyDownEvent event) {
                that.onKeyDown(event);
            }}));

        Event.addNativePreviewHandler(new NativePreviewHandler() {
            @Override
            public void onPreviewNativeEvent(NativePreviewEvent event) {
                NativeEvent nativeEvent = event.getNativeEvent();
                String type = nativeEvent.getType();
                if (type.equals(KeyDownEvent.getType().getName())){
                    onPreviewKeyDown(nativeEvent);
                }
                else if (type.equals(MouseDownEvent.getType().getName())) {
                    MouseDownEvent.fireNativeEvent(nativeEvent, worksheetPanel);
                }
            }
        });
    }

    private void addEditModeRegistrations()
    {
        this.editModeRegistrations.add(this.worksheetPanel.addDomHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                if (event.getRelativeX(worksheetPanel.getElement()) < 0 || event.getRelativeY(worksheetPanel.getElement()) < 0) {
                    return;
                }
                if (overToolFrames.isEmpty()) {
                    onClearAreaClicked(event);
                }
                else {
                    onOverToolFrameAreaClicked(event);
                }
            }
        }, MouseDownEvent.getType()));
    }

    private void onBackgroundImageSelected(ImageInformation arg)
    {
        if (this.pageOptions.backgroundImage.equals(arg))
        {
            return;
        }
        pageOptions.backgroundImage = arg;

        optionsUpdatedEvent.dispatch(pageOptions);
    }

    private void onCopyToolsRequest()
    {
    	this.copyToolsRequest.dispatch(new ArrayList<CanvasToolFrame>(selectedTools));
    }

    private void onPasteToolsRequest()
    {
    	this.pasteToolsRequest.dispatch(null);
    }

    private void onKeyDown(KeyDownEvent event){
        if (this.viewMode) {
            return;
        }
        switch (event.getNativeKeyCode())
        {
            case (int)'C':
                if (event.isControlKeyDown())
                {
                    this.onCopyToolsRequest();
                }
                break;
            case (int)'V':
                if (event.isControlKeyDown())
                {
                    this.onPasteToolsRequest();
                }
                break;
            case KeyCodes.KEY_DELETE:
                this.removeToolsRequest.dispatch(new ArrayList<CanvasToolFrame>(this.selectedTools));
                break;
            default:
                break;
        }
    }

    private void onPreviewKeyDown(NativeEvent event) {
        // TODO: Use some sort of KeyMapper.
    	switch (event.getKeyCode()) {
        case KeyCodes.KEY_ESCAPE:
            this.stopOperationEvent.dispatch(null);
            break;
        default:
            break;
        }
    }

    private void onClearAreaClicked(MouseDownEvent event) {
        toolFrameClickEvent.dispatch(null);

        if (null == this.activeToolboxItem) {
            return;
        }
        // TODO: should be handled by a tool outside the worksheet class?
        if (isSelectorActiveTool()) {
            this._toolFrameSelectionManager.startSelectionDrag(event);
        }
    }

    private boolean isSelectorActiveTool()
    {
        return (this.activeToolboxItem instanceof CursorToolboxItem) || (this.activeToolboxItem instanceof MoveToolboxItem);
    }

    private void onOverToolFrameAreaClicked(MouseDownEvent event)
    {
        if (this.activeToolboxItem instanceof MoveToolboxItem) {
            CanvasToolFrame highestToolUnderMouse = null;
            int highestZIndex = -1;
            for (CanvasToolFrame frame : this.overToolFrames) {
                int zIndex = ZIndexAllocator.getElementZIndex(frame.getElement());
                if (zIndex > highestZIndex) {
                    highestToolUnderMouse = frame;
                    highestZIndex = zIndex;
                }
            }
            this._toolFrameSelectionManager.forceToolFrameSelection(highestToolUnderMouse);
            this._toolFrameTransformer.startDragCanvasToolFrames(this.selectedTools, event);
            event.stopPropagation();
            event.preventDefault();
        }
    }


    private void changeStatusLabel(Button button, OperationStatus status, String pendingText, String doneText) {
        switch (status) {
        case PENDING:
            button.setText(pendingText);
            button.setEnabled(false);
            break;
        case SUCCESS:
        case FAILURE:
            button.setText(doneText);
            button.setEnabled(true);
        }
    }

    private void clearFloatingWidget() {
        if (null != this.floatingWidget) {
            this.worksheetPanel.remove(floatingWidget);
        }
        if (null != this._floatingWidgetTerminator) {
            this._floatingWidgetTerminator.onFire(null);
        }
        this.floatingWidget = null;
        this._floatingWidgetTerminator = null;
    }

    private void setActiveToolboxItemWithoutFloatingWidget(final ToolboxItem toolboxItem) {
        final HandlerRegistration createInstanceReg = this.worksheetPanel.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (overToolFrames.isEmpty()) {
                    Point2D position = ElementUtils.relativePosition(event, worksheetPanel.getElement());
                    toolCreationRequestEvent.dispatch(new ToolCreationRequest(position, toolboxItem
                            .getToolFactory()));
                }
            }
        }, ClickEvent.getType());
        this._floatingWidgetTerminator = new Handler<Void>() {
            @Override
            public void onFire(Void arg) {
                createInstanceReg.removeHandler();
            }
        };
    }

    private void setFloatingWidgetForTool(
            CanvasToolFactory<? extends CanvasTool<? extends ElementData>> factory) {
        this.floatingWidget = factory.getFloatingWidget();
        if (null == this.floatingWidget) {
            return;
        }
        this.worksheetPanel.add(floatingWidget);
        floatingWidget.addStyleName(CanvasResources.INSTANCE.main().floatingToolStyle());
        Event event = Event.getCurrentEvent();
        if (null != event) {
            Point2D relativeToWorksheet = new Point2D(event.getClientX(), event.getClientY());
            Point2D worksheetPos = ElementUtils.getElementAbsolutePosition(worksheetPanel.getElement());
            ElementUtils.setElementPosition(floatingWidget.getElement(),
                    Point2D.max(Point2D.zero, relativeToWorksheet.minus(worksheetPos)));
        }
    }

    private void startDraggingFloatingWidget(final ToolboxItem toolboxItem) {
        final WorksheetViewImpl that = this;
        Handler<Point2D> floatingWidgetMoveHandler = new Handler<Point2D>() {
            @Override
            public void onFire(Point2D arg) {
                ElementUtils.setElementPosition(that.floatingWidget.getElement(), arg);
            }
        };
        Handler<Point2D> floatingWidgetStop = new Handler<Point2D>() {
            @Override
            public void onFire(Point2D position) {
                toolCreationRequestEvent.dispatch(
                        new ToolCreationRequest(position, toolboxItem.getToolFactory()));
            }
        };
        this._floatingWidgetTerminator = this._floatingWidgetDragManager.startMouseMoveOperation(
                null, this.worksheetPanel.getElement(), Point2D.zero,
                floatingWidgetMoveHandler, floatingWidgetStop, null,
                ElementDragManager.StopCondition.STOP_CONDITION_MOUSE_CLICK);
    }

    @Override
    public void setToolFrameTransform(final CanvasToolFrame toolFrame, final Transform2D transform,
            final Point2D additionalOffset) {
        if (toolFrame.getTool().canRotate()) {
            ElementUtils.setRotation(toolFrame.getElement(), transform.rotation);
        }
        if (null != transform.size) {
            toolFrame.setToolSize(transform.size);
        }
        _toolFrameTransformer.setToolFramePosition(toolFrame, transform.translation.plus(additionalOffset));
    }

    @Override
    public ArrayList<CanvasToolFrame> getToolFrames() {
        return new ArrayList<CanvasToolFrame>(this.toolFrameRegistrations.keySet());
    }

    @Override
    public HandlerRegistration addCopyToolHandler(Handler<ArrayList<CanvasToolFrame>> handler) {
        return this.copyToolsRequest.addHandler(handler);
    }

    @Override
    public HandlerRegistration addPasteToolHandler(Handler<Void> handler) {
        return this.pasteToolsRequest.addHandler(handler);
    }

    @Override
    public void setUserProfile(UserProfile userProfile)
    {
        boolean canInvite = false;
        String publicName = "Guest";
        String email = "";
        if (null != userProfile) {
            canInvite = userProfile.canInvite;
            publicName = userProfile.publicName;
            email = userProfile.email;
        }
        this.linkInvite.setVisible(canInvite);
        this.userWelcomeLabel.setText(publicName);
        this.userWelcomeLabel.setTitle(email);
    }
}
