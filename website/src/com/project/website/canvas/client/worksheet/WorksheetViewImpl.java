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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.client.handlers.RegistrationsManager;
import com.project.shared.client.utils.ElementUtils;
import com.project.shared.client.utils.EventUtils;
import com.project.shared.client.utils.HandlerUtils;
import com.project.shared.data.Point2D;
import com.project.shared.utils.CloneableUtils;
import com.project.shared.utils.IterableUtils;
import com.project.shared.utils.ObjectUtils;
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
import com.project.website.canvas.client.shared.UndoManager;
import com.project.website.canvas.client.shared.ZIndexAllocator;
import com.project.website.canvas.client.shared.dialogs.SelectImageDialog;
import com.project.website.canvas.client.shared.searchProviders.SearchProviders;
import com.project.website.canvas.client.shared.widgets.DialogWithZIndex;
import com.project.website.canvas.client.worksheet.interfaces.ElementDragManager;
import com.project.website.canvas.client.worksheet.interfaces.MouseMoveOperationHandler;
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
    Anchor saveButton;

    @UiField
    Hyperlink viewButton;

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
    private final SimpleEvent<Void> undoRequestEvent = new SimpleEvent<Void>();
    private final SimpleEvent<ArrayList<CanvasToolFrame>> removeToolsRequest = new SimpleEvent<ArrayList<CanvasToolFrame>>();
    private final SimpleEvent<ArrayList<CanvasToolFrame>> copyToolsRequest = new SimpleEvent<ArrayList<CanvasToolFrame>>();
    private final SimpleEvent<Void> pasteToolsRequest = new SimpleEvent<Void>();
    private final SimpleEvent<ToolCreationRequest> toolCreationRequestEvent = new SimpleEvent<ToolCreationRequest>();
    private final SimpleEvent<CanvasToolFrame> activeToolFrameChangedEvent = new SimpleEvent<CanvasToolFrame>();

    private HashSet<CanvasToolFrame> selectedTools = new HashSet<CanvasToolFrame>();

    private boolean _viewMode;
    private boolean viewModeSet = false;


    public WorksheetViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));

        this.createDefaultPageOptions();

        this._toolFrameTransformer = new ToolFrameTransformerImpl(worksheetPanel, dragPanel, stopOperationEvent);
        this.dragPanel.setVisible(false);

        this._toolFrameSelectionManager = new ToolFrameSelectionManager(this, worksheetPanel, dragPanel, selectionPanel, stopOperationEvent);
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

    @Override
    public HandlerRegistration addActiveToolFrameChangedHandler(Handler<CanvasToolFrame> handler) {
        return this.activeToolFrameChangedEvent.addHandler(handler);
    }

    @Override
    public HandlerRegistration addCopyToolHandler(Handler<ArrayList<CanvasToolFrame>> handler) {
        return this.copyToolsRequest.addHandler(handler);
    }

    @Override
    public HandlerRegistration addInviteHandler(Handler<Void> handler)
    {
        return this.linkInvite.addClickHandler(HandlerUtils.asClickHandler(handler));
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
    public HandlerRegistration addLogoutHandler(Handler<Void> handler) {
        return this.linkLogout.addClickHandler(HandlerUtils.asClickHandler(handler));
    }

    @Override
    public HandlerRegistration addOptionsUpdatedHandler(Handler<CanvasPageOptions> handler) {
        return optionsUpdatedEvent.addHandler(handler);
    }

    @Override
    public HandlerRegistration addPasteToolHandler(Handler<Void> handler) {
        return this.pasteToolsRequest.addHandler(handler);
    }

    @Override
    public HandlerRegistration addRemoveToolsRequest(Handler<ArrayList<CanvasToolFrame>> handler) {
        return this.removeToolsRequest.addHandler(handler);
    }

    @Override
    public HandlerRegistration addSaveHandler(Handler<Void> handler) {
        return saveButton.addClickHandler(HandlerUtils.asClickHandler(handler));
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
    public void addToolInstanceWidget(final CanvasToolFrame toolFrame, final Transform2D transform, final Point2D additionalOffset)
    {
        this.toolFrameRegistrations.put(toolFrame, new RegistrationsManager());

        final RegistrationsManager tempRegs = new RegistrationsManager();
        tempRegs.add(toolFrame.asWidget().addAttachHandler(new AttachEvent.Handler() {
            @Override public void onAttachOrDetach(AttachEvent event) {
                if (event.isAttached()) {
                    setToolFrameTransform(toolFrame, transform, additionalOffset);
                    tempRegs.clear();
                }
            }
        }));

        if (false == this._viewMode)
        {
            this.setToolFrameRegistrations(toolFrame);
        }

        this.worksheetPanel.add(toolFrame);
    }

    @Override
    public HandlerRegistration addUndoRequestHandler(Handler<Void> handler)
    {
        return this.undoRequestEvent.addHandler(handler);
    }

    @Override
    public void clearActiveToolboxItem() {
        clearFloatingWidget();

        if (this.activeToolboxItem instanceof MoveToolboxItem)
        {
            for (CanvasToolFrame toolFrame : this.overToolFrames)
            {
                toolFrame.asWidget().removeStyleName(CanvasResources.INSTANCE.main().drag());
            }
        }

        if (null != this.activeToolboxItem) {
            this.worksheetPanel.removeStyleName(this.activeToolboxItem.getCanvasStyleInCreateMode());
            this.activeToolboxItem = null;
        }
    }

    @Override
    public void clearToolFrameSelection() {
        ArrayList<CanvasToolFrame> framesToClear = new ArrayList<CanvasToolFrame>(this.selectedTools);
        for (CanvasToolFrame toolFrame : framesToClear) {
            this.unSelectToolFrame(toolFrame);
        }
    }

    @Override
    public ArrayList<CanvasToolFrame> getToolFrames() {
        return new ArrayList<CanvasToolFrame>(this.toolFrameRegistrations.keySet());
    }

    @Override
    public boolean isToolFrameSelected(CanvasToolFrame toolFrame) {
        return this.selectedTools.contains(toolFrame);
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
        this.removeOverToolFrame(toolFrame);
        this.selectedTools.remove(toolFrame);
        RegistrationsManager regs = toolFrameRegistrations.remove(toolFrame);
        if (null != regs) {
            regs.clear();
        }
    }

    @Override
    public void selectToolFrame(CanvasToolFrame toolFrame) {
        this.selectedTools.add(toolFrame);
        toolFrame.asWidget().addStyleName(CanvasResources.INSTANCE.main().selected());
    }

    @Override
    public void setActiveToolboxItem(final ToolboxItem toolboxItem) {
        this.clearActiveToolboxItem();
        this.activeToolboxItem = toolboxItem;
        this.worksheetPanel.addStyleName(toolboxItem.getCanvasStyleInCreateMode());

        if (toolboxItem instanceof MoveToolboxItem) {
            // De-activate any active tools when entering Move mode
            this.activeToolFrameChangedEvent.dispatch(null);
        }

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
    public void setToolFrameTransform(final CanvasToolFrame toolFrame, final Transform2D transform,
            final Point2D additionalOffset) {
        if (toolFrame.getTool().canRotate()) {
            ElementUtils.setRotation(toolFrame.asWidget().getElement(), transform.rotation);
        }
        if (null != transform.size) {
            toolFrame.setToolSize(transform.size);
        }
        _toolFrameTransformer.setToolFramePosition(toolFrame, transform.translation.plus(additionalOffset));
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

    @Override
    public void setViewLinkTargetHistoryToken(String targetHistoryToken)
    {
        this.viewButton.setTargetHistoryToken(targetHistoryToken);
    }

    @Override
    public void setViewMode(boolean isViewMode) {
        if (this.viewModeSet && (this._viewMode == isViewMode)) {
            return;
        }
        this._viewMode = isViewMode;
        this.viewModeSet = true;
        for (CanvasToolFrame frame : this.toolFrameRegistrations.keySet()) {
            frame.setViewMode(isViewMode);
        }
        if (isViewMode) {
            this.clearEditModeRegistrations();

            this.worksheetHeader.addStyleName(CanvasResources.INSTANCE.main().displayNone());
            this.addStyleName(CanvasResources.INSTANCE.main().worksheetFullView());
            this.addStyleName(CanvasResources.INSTANCE.main().worksheetModeViewOnly());
            this.removeStyleName(CanvasResources.INSTANCE.main().worksheetModeEditable());

        } else {
            this.addEditModeRegistrations();

            this.worksheetHeader.removeStyleName(CanvasResources.INSTANCE.main().displayNone());
            this.removeStyleName(CanvasResources.INSTANCE.main().worksheetFullView());
            this.removeStyleName(CanvasResources.INSTANCE.main().worksheetModeViewOnly());
            this.addStyleName(CanvasResources.INSTANCE.main().worksheetModeEditable());
        }
    }

    @Override
    public void unSelectToolFrame(CanvasToolFrame toolFrame) {
        this.selectedTools.remove(toolFrame);
        toolFrame.asWidget().removeStyleName(CanvasResources.INSTANCE.main().selected());
    }

    private void addEditModeRegistrations()
    {
        final WorksheetViewImpl that = this;
        this.editModeRegistrations.add(this.worksheetPanel.addDomHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                /**
                 * TODO: don't use getRelativeX/Y, see ElementUtils.relativePosition
                 * */
                if (event.getRelativeX(that.worksheetPanel.getElement()) < 0
                 || event.getRelativeY(that.worksheetPanel.getElement()) < 0)
                {
                    return;
                }
                if (that.overToolFrames.isEmpty()) {
                    onClearAreaClicked(event);
                }
                else {
                    onOverToolFrameAreaClicked(event);
                }
            }
        }, MouseDownEvent.getType()));

        for (CanvasToolFrame toolFrame : this.toolFrameRegistrations.keySet()) {
            this.setToolFrameRegistrations(toolFrame);
        }
    }

    private void addOverToolFrame(final CanvasToolFrame toolFrame)
    {
        overToolFrames.add(toolFrame);
        if (activeToolboxItem instanceof MoveToolboxItem) {
            toolFrame.setDragging(true);
        }
    }

    private void addRegistrations() {
        this.addEditModeRegistrations();
        final WorksheetViewImpl that = this;

        this.allModesRegistrations.add(this.optionsBackground.addClickHandler(new ClickHandler() {
            @Override public void onClick(ClickEvent event) {
                selectImageDialog.setValue(
                    (ImageInformation)CloneableUtils.clone(pageOptions.backgroundImage));
                optionsDialog.center();
            }
        }));
        this.allModesRegistrations.add(this.selectImageDialog.addCancelHandler(new SimpleEvent.Handler<Void>() {
            @Override public void onFire(Void arg) {
                optionsDialog.hide();
            }
        }));
        this.allModesRegistrations.add(this.selectImageDialog.addDoneHandler(new SimpleEvent.Handler<ImageInformation>() {
            @Override public void onFire(ImageInformation arg) {
                optionsDialog.hide();
                onBackgroundImageSelected(arg);
            }
        }));
        this.allModesRegistrations.add(this.focusPanel.addKeyDownHandler(new KeyDownHandler(){
            @Override public void onKeyDown(KeyDownEvent event) {
                that.onWorksheetFocusedKeyDown(event);
            }}));
        this.allModesRegistrations.add(Event.addNativePreviewHandler(new NativePreviewHandler() {
            @Override public void onPreviewNativeEvent(NativePreviewEvent event) {
                NativeEvent nativeEvent = null == event ? null : event.getNativeEvent();
                if (null == nativeEvent) {
                    return;
                }
                that.handleAllModesPreviewEvent(event);
                if (false == that._viewMode) {
                    that.handleEditModePreviewEvent(event);
                }
            }
        }));
    }

    private void changeStatusLabel(Anchor button, OperationStatus status, String pendingText, String doneText) {
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

    private void clearEditModeRegistrations()
    {
        this.editModeRegistrations.clear();
        for (RegistrationsManager regs : this.toolFrameRegistrations.values()) {
            regs.clear();
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


    private void createDefaultPageOptions()
    {
        this.pageOptions = new CanvasPageOptions();
        ImageOptionsProviderUtils.setImageOptions(this._imageOptionsProvider, pageOptions.backgroundImage.options, ImageOptionTypes.OriginalSize);
    }

    private void handleAllModesPreviewEvent(NativePreviewEvent event)
    {
        if (EventUtils.nativePreviewEventTypeEquals(event, KeyDownEvent.getType()))
        {
            final WorksheetViewImpl that = this;
            that.onPreviewKeyDown(event);
        }
    }

    private void handleEditModePreviewEvent(NativePreviewEvent event)
    {
        NativeEvent nativeEvent = event.getNativeEvent();
        if (this.activeToolboxItem instanceof MoveToolboxItem) {
            if (EventUtils.nativePreviewEventTypeEquals(event, MouseDownEvent.getType()))
            {
                MouseDownEvent.fireNativeEvent(nativeEvent, this.worksheetPanel);
            }
            else if (EventUtils.nativePreviewEventTypeEquals(event, MouseUpEvent.getType()))
            {
                MouseUpEvent.fireNativeEvent(nativeEvent, this.worksheetPanel);
            }
        }
    }

    private boolean isSelectorActiveTool()
    {
        return (this.activeToolboxItem instanceof CursorToolboxItem) || (this.activeToolboxItem instanceof MoveToolboxItem);
    }

    private void onBackgroundImageSelected(ImageInformation arg)
    {
        if (ObjectUtils.areEqual(this.pageOptions.backgroundImage, arg))
        {
            return;
        }
        pageOptions.backgroundImage = arg;

        optionsUpdatedEvent.dispatch(pageOptions);
    }

    private void onClearAreaClicked(MouseDownEvent event) {
        this.activeToolFrameChangedEvent.dispatch(null);

        if (null == this.activeToolboxItem) {
            return;
        }

        // TODO: should be handled by a tool outside the worksheet class?
        if (this.isSelectorActiveTool()) {
            this._toolFrameSelectionManager.startSelectionDrag(event);
        }
    }

    private void onCopyToolsRequest()
    {
    	this.copyToolsRequest.dispatch(new ArrayList<CanvasToolFrame>(selectedTools));
    }

    private void onPreviewKeyDown(NativePreviewEvent event)
    {
        if (event.getNativeEvent().getCtrlKey())
        {
            switch (event.getNativeEvent().getKeyCode())
            {
                case (int)'Z':
                    UndoManager.get().undo();
                    return;
                case (int)'Y':
                    UndoManager.get().redo();
                    return;
                default:
                    //do nothing
                    break;
            }
        }
        switch (event.getNativeEvent().getKeyCode())
        {
            case KeyCodes.KEY_ESCAPE:
                stopOperationEvent.dispatch(null);
                break;
            default:
                break;
        }
    }

    private void onWorksheetFocusedKeyDown(KeyDownEvent event){
        if (this._viewMode) {
            return;
        }
        if (event.isControlKeyDown())
        {
            switch (event.getNativeKeyCode())
            {
                case (int)'C':
                    this.onCopyToolsRequest();
                    event.preventDefault();
                    return;
                case (int)'V':
                    this.onPasteToolsRequest();
                    event.preventDefault();
                    return;
                default:
                    //do nothing
                    break;
            }
        }
        switch (event.getNativeKeyCode())
        {
            case KeyCodes.KEY_DELETE:
                this.removeToolsRequest.dispatch(new ArrayList<CanvasToolFrame>(this.selectedTools));
                event.preventDefault();
                break;
            default:
                break;
        }
    }

    private void onOverToolFrameAreaClicked(MouseDownEvent event)
    {
        if (false == (this.activeToolboxItem instanceof MoveToolboxItem)) {
            return;
        }
        CanvasToolFrame highestToolUnderMouse = null;
        int highestZIndex = -1;
        for (CanvasToolFrame frame : this.overToolFrames) {
            int zIndex = ZIndexAllocator.getElementZIndex(frame.asWidget().getElement());
            if (zIndex > highestZIndex) {
                highestToolUnderMouse = frame;
                highestZIndex = zIndex;
            }
        }
        this._toolFrameSelectionManager.forceToolFrameSelection(highestToolUnderMouse);
        this.startDraggingSelectedToolFrames(event);
        event.stopPropagation();
        event.preventDefault();
    }

    private void onPasteToolsRequest()
    {
    	this.pasteToolsRequest.dispatch(null);
    }

    private void removeOverToolFrame(final CanvasToolFrame toolFrame)
    {
        overToolFrames.remove(toolFrame);
        if (activeToolboxItem instanceof MoveToolboxItem) {
            toolFrame.setDragging(false);
        }
    }

    private void setActiveToolboxItemWithoutFloatingWidget(final ToolboxItem toolboxItem) {
        final HandlerRegistration createInstanceReg = this.worksheetPanel.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (overToolFrames.isEmpty()) {
                    Point2D position = ElementUtils.getRelativePosition(event, worksheetPanel.getElement());
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
            ElementUtils.setElementCSSPosition(floatingWidget.getElement(),
                    Point2D.max(Point2D.zero, relativeToWorksheet.minus(worksheetPos)));
        }
    }

    private void setToolFrameRegistrations(final CanvasToolFrame toolFrame)
    {
        RegistrationsManager regs = this.toolFrameRegistrations.get(toolFrame);

        // In case we have already registered.
        regs.clear();

        regs.add(toolFrame.addMoveStartRequestHandler(new SimpleEvent.Handler<MouseEvent<?>>() {
            @Override public void onFire(MouseEvent<?> arg) {
                _toolFrameSelectionManager.forceToolFrameSelection(toolFrame);
                startDraggingSelectedToolFrames(arg);
            }
        }));
        regs.add(toolFrame.addResizeStartRequestHandler(new SimpleEvent.Handler<MouseEvent<?>>() {
            @Override public void onFire(MouseEvent<?> arg) {
                _toolFrameSelectionManager.forceToolFrameSelection(toolFrame);
                _toolFrameTransformer.startResizeCanvasToolFrame(toolFrame, arg);
            }
        }));
        if (toolFrame.getTool().canRotate()) {
            regs.add(toolFrame.addRotateStartRequestHandler(new SimpleEvent.Handler<MouseEvent<?>>() {
                @Override public void onFire(MouseEvent<?> arg) {
                    _toolFrameSelectionManager.forceToolFrameSelection(toolFrame);
                    _toolFrameTransformer.startRotateCanvasToolFrame(toolFrame, arg);
                }
            }));
        }
        regs.add(toolFrame.addFocusHandler(new FocusHandler() {
            @Override public void onFocus(FocusEvent event) {
                activeToolFrameChangedEvent.dispatch(toolFrame);
            }
        }));
        regs.add(toolFrame.asWidget().addDomHandler(new MouseOverHandler() {
            @Override public void onMouseOver(MouseOverEvent event) {
                addOverToolFrame(toolFrame);
            }
        }, MouseOverEvent.getType()));
        regs.add(toolFrame.asWidget().addDomHandler(new MouseOutHandler() {
            @Override public void onMouseOut(MouseOutEvent event) {
                removeOverToolFrame(toolFrame);
            }
        }, MouseOutEvent.getType()));
        regs.add(toolFrame.addMouseUpHandler(new MouseUpHandler() {
            @Override public void onMouseUp(MouseUpEvent event) {
                _toolFrameSelectionManager.handleToolFrameSelection(toolFrame);
            }
        }));
    }

    private void startDraggingFloatingWidget(final ToolboxItem toolboxItem) {
        final WorksheetViewImpl that = this;
        MouseMoveOperationHandler handler = new MouseMoveOperationHandler() {
            @Override public void onStop(Point2D pos) {
                toolCreationRequestEvent.dispatch(
                        new ToolCreationRequest(pos, toolboxItem.getToolFactory()));
            }

            @Override public void onStart() {
            }

            @Override public void onMouseMove(Point2D pos) {
                ElementUtils.setElementCSSPosition(that.floatingWidget.getElement(), pos);
            }

            @Override public void onCancel() {
            }
        };
        this._floatingWidgetTerminator = this._floatingWidgetDragManager.startMouseMoveOperation(
                null, this.worksheetPanel.getElement(), Point2D.zero,
                handler, ElementDragManager.StopCondition.STOP_CONDITION_MOUSE_CLICK);
    }

    private void startDraggingSelectedToolFrames(MouseEvent<?> arg)
    {
        _toolFrameTransformer.startDragCanvasToolFrames(IterableUtils.<CanvasToolFrame, CanvasToolFrame>upCast(selectedTools), arg);
    }
}
