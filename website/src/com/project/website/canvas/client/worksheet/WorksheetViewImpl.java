package com.project.website.canvas.client.worksheet;

import java.util.ArrayList;
import java.util.HashSet;

import com.google.common.base.Objects;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DataTransfer;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.DragEnterEvent;
import com.google.gwt.event.dom.client.DragEnterHandler;
import com.google.gwt.event.dom.client.DragEvent;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HumanInputEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
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
import com.project.shared.client.utils.SchedulerUtils;
import com.project.shared.client.utils.ZIndexAllocator;
import com.project.shared.client.utils.widgets.DialogWithZIndex;
import com.project.shared.client.utils.widgets.WidgetUtils;
import com.project.shared.data.Point2D;
import com.project.shared.data.funcs.Func;
import com.project.shared.utils.ArrayUtils;
import com.project.shared.utils.IterableUtils;
import com.project.shared.utils.loggers.Logger;
import com.project.website.canvas.client.canvastools.CursorToolboxItem;
import com.project.website.canvas.client.canvastools.MoveToolboxItem;
import com.project.website.canvas.client.canvastools.base.interfaces.CanvasTool;
import com.project.website.canvas.client.canvastools.base.interfaces.CanvasToolFactory;
import com.project.website.canvas.client.canvastools.base.interfaces.CanvasToolFrame;
import com.project.website.canvas.client.canvastools.base.interfaces.ToolboxItem;
import com.project.website.canvas.client.resources.CanvasResources;
import com.project.website.canvas.client.shared.ImageInformationUtils;
import com.project.website.canvas.client.shared.UndoManager;
import com.project.website.canvas.client.shared.UndoManager.UndoRedoPair;
import com.project.website.canvas.client.shared.dialogs.SelectImageDialog;
import com.project.website.canvas.client.shared.searchProviders.SearchProviders;
import com.project.website.canvas.client.worksheet.data.CanvasToolFrameInfo;
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

//    @UiField
//    Anchor linkInvite;

    @UiField
    Anchor saveButton;
    
    @UiField
    Anchor newButton;

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

//    @UiField
//    Label userWelcomeLabel;
    @UiField
    Anchor addSpaceButton;

    @UiField
    Label statusLabel;

    @UiField
    HTMLPanel dropTarget;

    @UiField
    CheckBox gridCheckBox;
    
    @UiField
    ToolFramesContainerImpl toolFramesContainer;

    private static final Point2D PAGE_SIZE_ADDITIONAL_AMOUNT = new Point2D(0, 300);
    private static final int PAGE_SIZE_ADD_ANIMATION_DURATION = 500;
    
    public static final String DEFAULT_PUBLIC_NAME = "Guest";

    private static final String SAVE_PAGE_EDITABLE = "Save";
    private static final String SAVE_PAGE_NON_EDITABLE = "Save as new page";

    private ToolboxItem _activeToolboxItem;
    private Widget _floatingWidget;
    private CanvasPageOptions _pageOptions;

    private final SimpleEvent<Void> _floatingWidgetTerminated = new SimpleEvent<Void>();

    private final WorksheetImageOptionsProvider _imageOptionsProvider = new WorksheetImageOptionsProvider();

    private final ToolFrameTransformer _toolFrameTransformer;
    private final ToolFrameSelectionManager _toolFrameSelectionManager;
    private final ElementDragManagerImpl _floatingWidgetDragManager;

    private final DialogBox _optionsDialog = new DialogWithZIndex(false, true);
    private final SelectImageDialog _selectImageDialog = new SelectImageDialog();
    
    private final RegistrationsManager _editModeRegistrations = new RegistrationsManager();
    private final RegistrationsManager _allModesRegistrations = new RegistrationsManager();


    private final SimpleEvent<CanvasPageOptions> _optionsUpdatedEvent = new SimpleEvent<CanvasPageOptions>();
    private final SimpleEvent<Void> _stopOperationEvent = new SimpleEvent<Void>();
    private final SimpleEvent<Void> _undoRequestEvent = new SimpleEvent<Void>();
    private final SimpleEvent<ImageDropInfo> _imageDropEvent = new SimpleEvent<ImageDropInfo>();
    private final SimpleEvent<ArrayList<CanvasToolFrame>> _removeToolsRequest = new SimpleEvent<ArrayList<CanvasToolFrame>>();
    private final SimpleEvent<ArrayList<CanvasToolFrame>> _copyToolsRequest = new SimpleEvent<ArrayList<CanvasToolFrame>>();
    private final SimpleEvent<Void> _pasteToolsRequest = new SimpleEvent<Void>();
    private final SimpleEvent<ToolCreationRequest> _toolCreationRequestEvent = new SimpleEvent<ToolCreationRequest>();
    private final SimpleEvent<CanvasToolFrame> _activeToolFrameChangedEvent = new SimpleEvent<CanvasToolFrame>();

    private HashSet<CanvasToolFrame> _selectedTools = new HashSet<CanvasToolFrame>();

    private final boolean _dragSupported = DragEvent.isSupported();

    private boolean _viewMode;
    private boolean _modeInitialized = false;

    private boolean _pageEditable;



    public WorksheetViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));

        this._toolFrameTransformer = new ToolFrameTransformerImpl(worksheetPanel, dragPanel, _stopOperationEvent);
        this.dragPanel.setVisible(false);
        this.dropTarget.setVisible(false);

        this._toolFrameSelectionManager = new ToolFrameSelectionManager(this, worksheetPanel, dragPanel, selectionPanel, _stopOperationEvent);
        this.selectionPanel.setVisible(false);

        this._floatingWidgetDragManager = new ElementDragManagerImpl(this, this.dragPanel, 0, _stopOperationEvent);

        this._optionsDialog.setText("Worksheet options");
        this._selectImageDialog.setImageOptionsProvider(this._imageOptionsProvider);
        this._selectImageDialog.setSearchProviders(SearchProviders.getDefaultImageSearchProviders());
        this._optionsDialog.add(this._selectImageDialog);

        this.addRegistrations();
        this.setViewMode(false);
        
        // TODO: remove when users feature is fully implemented
        this.linkLogout.setVisible(false);
    }

    @Override
    public HandlerRegistration addActiveToolFrameChangedHandler(Handler<CanvasToolFrame> handler) {
        return this._activeToolFrameChangedEvent.addHandler(handler);
    }

    @Override
    public HandlerRegistration addCopyToolHandler(Handler<ArrayList<CanvasToolFrame>> handler) {
        return this._copyToolsRequest.addHandler(handler);
    }

    @Override
    public HandlerRegistration addInviteHandler(Handler<Void> handler)
    {
//        return this.linkInvite.addClickHandler(HandlerUtils.asClickHandler(handler));
        return null;
    }

    @Override
    public HandlerRegistration addLoadHandler(final Handler<String> handler) {
        // TODO: why is this 'return null' here?
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
        return _optionsUpdatedEvent.addHandler(handler);
    }

    @Override
    public HandlerRegistration addPasteToolHandler(Handler<Void> handler) {
        return this._pasteToolsRequest.addHandler(handler);
    }

    @Override
    public HandlerRegistration addRemoveToolsRequest(Handler<ArrayList<CanvasToolFrame>> handler) {
        return this._removeToolsRequest.addHandler(handler);
    }
    
    @Override
    public HandlerRegistration addAddSpaceHandler(Handler<Void> handler) {
        return this.addSpaceButton.addClickHandler(HandlerUtils.asClickHandler(handler));
    }
    
    @Override
    public HandlerRegistration addImageDropHandler(Handler<ImageDropInfo> handler) {
        return this._imageDropEvent.addHandler(handler);
    }
    


    @Override
    public HandlerRegistration addSaveHandler(Handler<Void> handler) {
        return saveButton.addClickHandler(HandlerUtils.asClickHandler(handler));
    }

    @Override
    public HandlerRegistration addNewPageHandler(Handler<Void> handler) {
        return newButton.addClickHandler(HandlerUtils.asClickHandler(handler));
    }

    @Override
    public HandlerRegistration addStopOperationHandler(Handler<Void> handler) {
        return _stopOperationEvent.addHandler(handler);
    }

    @Override
    public HandlerRegistration addToolCreationRequestHandler(Handler<ToolCreationRequest> handler) {
        return _toolCreationRequestEvent.addHandler(handler);
    }

    @Override
    public void addToolInstanceWidget(final CanvasToolFrame toolFrame, final Transform2D transform, final Point2D additionalOffset, final boolean addFrameInnerOffset)
    {
        CanvasToolFrameInfo info = this.toolFramesContainer.addToolFrame(toolFrame);
        this.setToolFrameRegistrations(toolFrame, info.getRegistrations().asRegistrationsManager(this));

        WidgetUtils.getOnAttachAsyncFunc(toolFrame.asWidget())
		   .then(SchedulerUtils.getDeferredAsyncFunc())
		   .then(new Func.VoidAction() {
			   @Override public void exec() {
				   final Point2D innerFrameOffset = addFrameInnerOffset ? toolFrame.getToolOffsetInFrame() : Point2D.zero;
				   setToolFrameTransform(toolFrame, transform, additionalOffset.minus(innerFrameOffset));
			}})
			.run(null);
    }

    @Override
    public HandlerRegistration addUndoRequestHandler(Handler<Void> handler)
    {
        return this._undoRequestEvent.addHandler(handler);
    }

    @Override
    public void clearActiveToolboxItem() {
        clearFloatingWidget();

        if (this._activeToolboxItem instanceof MoveToolboxItem)
        {
            // todo re-add in canvas?
//            for (CanvasToolFrame toolFrame : this._overToolFrames)
//            {
//                toolFrame.asWidget().removeStyleName(CanvasResources.INSTANCE.main().drag());
//            }
        }

        if (null != this._activeToolboxItem) {
            this.worksheetPanel.removeStyleName(this._activeToolboxItem.getCanvasStyleInCreateMode());
            this._activeToolboxItem = null;
        }
    }

    public void selectAllTools() {
        for (CanvasToolFrame toolFrame : this.getToolFrames())
            this.selectToolFrame(toolFrame);
    }

    @Override
    public void clearToolFrameSelection() {
        ArrayList<CanvasToolFrame> framesToClear = new ArrayList<CanvasToolFrame>(this._selectedTools);
        for (CanvasToolFrame toolFrame : framesToClear) {
            this.unSelectToolFrame(toolFrame);
        }
    }

    @Override
    public ArrayList<CanvasToolFrame> getToolFrames() {
        return IterableUtils.toArrayList(this.toolFramesContainer.getToolFrames());
    }

    @Override
    public boolean isToolFrameSelected(CanvasToolFrame toolFrame) {
        return this._selectedTools.contains(toolFrame);
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
        this.changeStatusLabel(saveButton, status, "Saving...", this.getSaveButtonText());
        if (OperationStatus.FAILURE == status) {
            Window.alert("Save failed. Reason: " + reason);
        }
    }

    @Override
    public void removeToolInstanceWidget(CanvasToolFrame toolFrame) {
        this.toolFramesContainer.removeToolFrame(toolFrame);
        this._selectedTools.remove(toolFrame);
    }

    @Override
    public void selectToolFrame(CanvasToolFrame toolFrame) {
        this._selectedTools.add(toolFrame);
        toolFrame.asWidget().addStyleName(CanvasResources.INSTANCE.main().selected());
    }

    @Override
    public void setActiveToolboxItem(final ToolboxItem toolboxItem) {
        this.clearActiveToolboxItem();
        this._activeToolboxItem = toolboxItem;
        this.worksheetPanel.addStyleName(toolboxItem.getCanvasStyleInCreateMode());

        // De-activate any active tools, except when entering Cursor mode
        if (false == (toolboxItem instanceof CursorToolboxItem)) {
            this._activeToolFrameChangedEvent.dispatch(null);
        }

        // also de-select any selected tools, unless a selector toolbox item was activated
        if (false == this.isSelectorActiveTool()) {
            this.clearToolFrameSelection();
        }

        CanvasToolFactory<? extends CanvasTool<? extends ElementData>> factory = toolboxItem.getToolFactory();
        if (null == factory) {
            return;
        }

        if (toolboxItem.createOnMouseDown()) {
            this.setActiveToolboxItemWithoutFloatingWidget(toolboxItem);
        }

        this.setFloatingWidgetForTool(factory);
        if (null == this._floatingWidget) {
            return;
        }

        this.startDraggingFloatingWidget(toolboxItem);
    }

    @Override
    public void setOptions(final CanvasPageOptions value) {
        this._pageOptions = value;

        this.worksheetBackground.addStyleName(CanvasResources.INSTANCE.main().imageLoadingStyle());
        this.pageSizeUpdated();

        WidgetUtils.setBackgroundImageAsync(this.worksheetBackground, value.backgroundImage.getUrl(),
                CanvasResources.INSTANCE.imageUnavailable().getSafeUri().asString(), false,
                CanvasResources.INSTANCE.main().imageLoadingStyle(),
                new SimpleEvent.Handler<Void>() {
                    @Override
                    public void onFire(Void arg) {
                        handleBackgroundImageSet();
                    }}, HandlerUtils.<Void>emptyHandler());
    }

    private void handleBackgroundImageSet()
    {
        ImageInformationUtils.setBackgroundStyle(this.worksheetBackground, this._pageOptions.backgroundImage);
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
//        boolean canInvite = false;
//        String publicName = "";
//        String email = "";
//        if (null != userProfile) {
//            canInvite = userProfile.canInvite;
//            publicName = userProfile.publicName;
//            email = userProfile.email;
//        }
        //this.linkInvite.setVisible(canInvite);
//        this.userWelcomeLabel.setText(
//                StringUtils.defaultIfNullOrEmpty(publicName, DEFAULT_PUBLIC_NAME));
//        this.userWelcomeLabel.setTitle(email);
    }

    @Override
    public void setViewLinkTargetHistoryToken(String targetHistoryToken)
    {
        this.viewButton.setTargetHistoryToken(targetHistoryToken);
    }

    @Override
    public void setViewMode(boolean isViewMode) {
        if (this._modeInitialized && (this._viewMode == isViewMode)) {
            return;
        }
        this._viewMode = isViewMode;
        this._modeInitialized = true;
        for (CanvasToolFrame frame : this.toolFramesContainer.getToolFrames()) {
            frame.setViewMode(isViewMode);
        }
        this.toolFramesContainer.setIsEditMode(false == isViewMode);
        if (isViewMode) {
            this.clearEditModeRegistrations();

            this.worksheetHeader.addStyleName(CanvasResources.INSTANCE.main().displayNone());
            this.addStyleName(CanvasResources.INSTANCE.main().worksheetFullView());
            this.addStyleName(CanvasResources.INSTANCE.main().worksheetModeViewOnly());
            this.removeStyleName(CanvasResources.INSTANCE.main().worksheetModeEditable());

        } else {
            this.setEditModeRegistrations();

            this.worksheetHeader.removeStyleName(CanvasResources.INSTANCE.main().displayNone());
            this.removeStyleName(CanvasResources.INSTANCE.main().worksheetFullView());
            this.removeStyleName(CanvasResources.INSTANCE.main().worksheetModeViewOnly());
            this.addStyleName(CanvasResources.INSTANCE.main().worksheetModeEditable());
        }
    }

    @Override
    public void unSelectToolFrame(CanvasToolFrame toolFrame) {
        this._selectedTools.remove(toolFrame);
        toolFrame.asWidget().removeStyleName(CanvasResources.INSTANCE.main().selected());
    }

    @Override
    protected void onLoad()
    {
        super.onLoad();
        ElementUtils.setTextSelectionEnabled(this.getElement(), false);
    }


    @Override
    public void setPageEditable(boolean isEditable) {
        this._pageEditable = isEditable;
        this.saveButton.setText(this.getSaveButtonText());
    }

    private String getSaveButtonText() {
        return this._pageEditable ? SAVE_PAGE_EDITABLE : SAVE_PAGE_NON_EDITABLE;
    }

    @Override
    public void pageSizeUpdated() {
        WidgetUtils.getOnAttachAsyncFunc(this.worksheetPanel)
                   .then(SchedulerUtils.getDeferredAsyncFunc())
                   .then(new Func.VoidAction() {
                       @Override public void exec() {
                           
                           performPageSizeUpdate();
                    }})
                   .run(null);
    }
    /*----------------------------------------------------------------------------------------*/

    private void performPageSizeUpdate() {
        // Never make the page height less than what is currently visible on the screen
        // to prevent truncation of the background before the vertical edge of the window
        int currentHeight = this.focusPanel.getOffsetHeight();
        Logger.info("Updating page size. Current height: " + String.valueOf(currentHeight));
        this.worksheetPanel.setHeight(String.valueOf(Math.max(currentHeight, this._pageOptions.size.getY())) + "px");
        this.worksheetBackground.setHeight(String.valueOf(Math.max(currentHeight, this._pageOptions.size.getY())) + "px");
    }

    private void setEditModeRegistrations()
    {
        final WorksheetViewImpl that = this;
        this._editModeRegistrations.clear();
        this._editModeRegistrations.add(this.worksheetPanel.addDomHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                Point2D posRelativeToWorksheet = ElementUtils.getMousePositionRelativeToElement(worksheetPanel.getElement());
                if (null == posRelativeToWorksheet) {
                    return;
                }
                if (posRelativeToWorksheet.getX() < 0 || posRelativeToWorksheet.getY() < 0)
                {
                    return;
                }
                if (that.toolFramesContainer.getHoveredToolFrames().isEmpty()) {
                    onClearAreaClicked(event);
                }
                else {
                    onOverToolFrameAreaClicked(event);
                }
            }
        }, MouseDownEvent.getType()));

        this._editModeRegistrations.add(this.addSpaceButton.addClickHandler(new ClickHandler() {
            @Override public void onClick(ClickEvent event) {
                onAddSpaceRequest();
            }
        }));
        if (this._dragSupported) {
            this._editModeRegistrations.add(this.addDomHandler(new DragEnterHandler() {
                @Override public void onDragEnter(DragEnterEvent event) {
                    event.preventDefault();
                    event.stopPropagation();
                    Logger.info("drag enter");
                    that.dropTarget.setVisible(true);
                }
            }, DragEnterEvent.getType()));
            this._editModeRegistrations.add(this.addDomHandler(new DragOverHandler() {
                @Override public void onDragOver(DragOverEvent event) {
                    event.preventDefault();
                    event.stopPropagation();
                    Logger.info("drag over");
                    that.dropTarget.setVisible(true);
                }
            }, DragOverEvent.getType()));
            this._editModeRegistrations.add(this.dropTarget.addDomHandler(new DropHandler() {
                @Override public void onDrop(DropEvent event) {
                    event.preventDefault();
                    event.stopPropagation();
                    Logger.info("drop");
                    that.dropTarget.setVisible(false);
                    that.onDropEvent(event);
                }
            }, DropEvent.getType()));
        }
        
    }

    protected void onDropEvent(DropEvent event) {
        DataTransfer dataTransfer = event.getDataTransfer();
        Logger.info(dataTransfer);
        this.handleDrop(dataTransfer, ElementUtils.getMousePositionRelativeToElement(toolFramesContainer.getElement()));
    }
    
    protected void createImageFromDataUrl(String dataUrl, Point2D pos)
    {
    	// TODO: Currently the image dataUrl is limited in size on the server side (google app engine limitation + the fact that we save base64 encoded data instead of binary)
    	this._imageDropEvent.dispatch(new ImageDropInfo(dataUrl, pos));
    }
    
    // see: http://www.html5rocks.com/en/tutorials/file/dndfiles/
    protected native void handleDrop(DataTransfer dataTransfer, Point2D pos) 
    /*-{
    	var _this = this;
    	var _pos = pos;
        var files = dataTransfer.files;
		// Loop through the FileList and render image files as thumbnails.
        for (var i = 0, f; f = files[i]; i++) 
        {
			$wnd.console.log(f.name + ', ' + (f.type || 'n/a') + ', ' + f.size + ' bytes, last modified: ' + (f.lastModifiedDate ? f.lastModifiedDate.toLocaleDateString() : 'n/a'));
			// Only process image files.
			if (!f.type.match('image.*')) {
				continue;
			}
			
			var reader = new FileReader();
			
			// Closure to capture the file information.
			reader.onload = (function(theFile) {
			  return function(e) {
			  	var dataUrl = e.target.result.toString();
				_this.@com.project.website.canvas.client.worksheet.WorksheetViewImpl::createImageFromDataUrl(Ljava/lang/String;Lcom/project/shared/data/Point2D;)(dataUrl, _pos);
			  };
			})(f);
			
			// Read in the image file as a data URL.
			reader.readAsDataURL(f);
	    }
    }-*/;
    
    
    protected void onAddSpaceRequest() {
        UndoManager.get().addAndRedo(this, new UndoRedoPair() {
            @Override
            public void undo() {
                performAddSpace(-1);
            }
            
            @Override
            public void redo() {
                performAddSpace(1);
            }
        });
    }

    private void performAddSpace(int direction) {
        Point2D transformVector = PAGE_SIZE_ADDITIONAL_AMOUNT.mul(direction);
        int newHeight = this.worksheetPanel.getOffsetHeight() + transformVector.getY();
        this.worksheetPanel.setHeight(String.valueOf(newHeight) + "px");
        this._pageOptions.size = new Point2D(this._pageOptions.size.getX(), newHeight);
        for (CanvasToolFrame toolFrame : this.toolFramesContainer.getToolFrames()) {
            Point2D newPos = ElementUtils.getElementOffsetPosition(toolFrame.asWidget().getElement())
                                         .plus(transformVector);
            this._toolFrameTransformer.setToolFramePosition(toolFrame, newPos, PAGE_SIZE_ADD_ANIMATION_DURATION);
        }
        this.pageSizeUpdated();
    }


    private void addRegistrations() {
        this.setEditModeRegistrations();
        final WorksheetViewImpl that = this;

        this._allModesRegistrations.add(this.optionsBackground.addClickHandler(new ClickHandler() {
            @Override public void onClick(ClickEvent event) {
                _selectImageDialog.setValue(_pageOptions.backgroundImage.getClone());
                _optionsDialog.center();
            }
        }));
        this._allModesRegistrations.add(this._selectImageDialog.addCancelHandler(new SimpleEvent.Handler<Void>() {
            @Override public void onFire(Void arg) {
                _optionsDialog.hide();
            }
        }));
        this._allModesRegistrations.add(this._selectImageDialog.addDoneHandler(new SimpleEvent.Handler<ImageInformation>() {
            @Override public void onFire(ImageInformation arg) {
                _optionsDialog.hide();
                onBackgroundImageSelected(arg);
            }
        }));
        this._allModesRegistrations.add(this.focusPanel.addKeyDownHandler(new KeyDownHandler(){
            @Override public void onKeyDown(KeyDownEvent event) {
                that.onWorksheetFocusedKeyDown(event);
            }}));
        this._allModesRegistrations.add(Event.addNativePreviewHandler(new NativePreviewHandler() {
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
        this._allModesRegistrations.add(this.gridCheckBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override public void onValueChange(ValueChangeEvent<Boolean> event) {
                that._toolFrameTransformer.setSnapToGrid(event.getValue());
            }
        }));
        
        this._allModesRegistrations.add(Window.addResizeHandler(new ResizeHandler() {
            @Override public void onResize(ResizeEvent event) {
                that.pageSizeUpdated();
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
        this._editModeRegistrations.clear();
        
    }

    private void clearFloatingWidget() {
        if (null != this._floatingWidget) {
            this.worksheetPanel.remove(_floatingWidget);
        }
        this._floatingWidgetTerminated.dispatch(null);
        this._floatingWidgetTerminated.clearAllHandlers();
        this._floatingWidget = null;
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
        if (this._activeToolboxItem instanceof MoveToolboxItem) {
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
        return (this._activeToolboxItem instanceof CursorToolboxItem) || (this._activeToolboxItem instanceof MoveToolboxItem);
    }

    private void onBackgroundImageSelected(ImageInformation arg)
    {
        if (Objects.equal(this._pageOptions.backgroundImage, arg))
        {
            return;
        }
        _pageOptions.backgroundImage = arg;

        _optionsUpdatedEvent.dispatch(_pageOptions);
    }

    private void onClearAreaClicked(HumanInputEvent<?> event) {
        this._activeToolFrameChangedEvent.dispatch(null);

        if (null == this._activeToolboxItem) {
            return;
        }

        // TODO: should be handled by a tool outside the worksheet class?
        if (this.isSelectorActiveTool()) {
            this._toolFrameSelectionManager.startSelectionDrag(event);
        }
    }

    private void onCopyToolsRequest()
    {
    	this._copyToolsRequest.dispatch(new ArrayList<CanvasToolFrame>(_selectedTools));
    }

    private void onPreviewKeyDown(NativePreviewEvent event)
    {
        if (event.getNativeEvent().getCtrlKey())
        {
            // TODO: When text is being edited, don't do this 
            switch (event.getNativeEvent().getKeyCode())
            {
                case (int)'Z':
                    UndoManager.get().undo();
                    return;
                case (int)'Y':
                    UndoManager.get().redo();
                    return;
                case (int)'A':
                    //Must be in the Preview handler since we want to cancel the event after handling it
                    //otherwise in some browsers the whole page is selected (e.g. firefox) and it interrupts dragging.
                    this.selectAllTools();
                    event.getNativeEvent().preventDefault();
                    return;
                default:
                    //do nothing
                    break;
            }
        }
        switch (event.getNativeEvent().getKeyCode())
        {
            case KeyCodes.KEY_ESCAPE:
                _stopOperationEvent.dispatch(null);

                //Prevent default otherwise all Animated Gifs (loading circle and user selected) are stopped.
                event.getNativeEvent().preventDefault();
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
                this._removeToolsRequest.dispatch(new ArrayList<CanvasToolFrame>(this._selectedTools));
                event.preventDefault();
                break;
            default:
                break;
        }
    }

    private void onOverToolFrameAreaClicked(DomEvent<?> event)
    {
        if (false == (this._activeToolboxItem instanceof MoveToolboxItem)) {
            return;
        }
        CanvasToolFrame highestToolUnderMouse = null;
        int highestZIndex = -1;
        for (CanvasToolFrame frame : this.toolFramesContainer.getHoveredToolFrames()) {
            int zIndex = ZIndexAllocator.getElementZIndex(frame.asWidget().getElement());
            if (zIndex > highestZIndex) {
                highestToolUnderMouse = frame;
                highestZIndex = zIndex;
            }
        }
        this._toolFrameSelectionManager.forceToolFrameSelection(highestToolUnderMouse);
        this.startDraggingSelectedToolFrames();
        event.stopPropagation();
        event.preventDefault();
    }

    private void onPasteToolsRequest()
    {
    	this._pasteToolsRequest.dispatch(null);
    }


    private void setActiveToolboxItemWithoutFloatingWidget(final ToolboxItem toolboxItem) {
        final RegistrationsManager regs = new RegistrationsManager();

        regs.add(WidgetUtils.addMovementStopHandler(this.worksheetPanel, new Handler<HumanInputEvent<?>>() {
            @Override public void onFire(HumanInputEvent<?> event) {
                dispatchToolCreationWithoutFloatingWidget(toolboxItem, event);
                event.stopPropagation();
                event.preventDefault();
            }}));

        this._floatingWidgetTerminated.addHandler(new Handler<Void>() {
            @Override public void onFire(Void arg) {
                regs.clear();
            }
        });
    }

    private void setFloatingWidgetForTool(CanvasToolFactory<? extends CanvasTool<? extends ElementData>> factory)
    {
        this._floatingWidget = factory.getFloatingWidget();
        if (null == this._floatingWidget) {
            return;
        }
        this.worksheetPanel.add(_floatingWidget);
        _floatingWidget.addStyleName(CanvasResources.INSTANCE.main().floatingToolStyle());
        Event event = Event.getCurrentEvent();
        if (null != event) {
            Point2D relativeToWorksheet = new Point2D(event.getClientX(), event.getClientY());
            Point2D worksheetPos = ElementUtils.getElementAbsolutePosition(worksheetPanel.getElement());
            ElementUtils.setElementCSSPosition(_floatingWidget.getElement(),
                    Point2D.max(Point2D.zero, relativeToWorksheet.minus(worksheetPos).plus(factory.getFloatingWidgetCreationOffset())));
        }
    }

    private void setToolFrameRegistrations(final CanvasToolFrame toolFrame, RegistrationsManager regs)
    {
        final WorksheetViewImpl that = this;
        // In case we have already registered.
        regs.addRecurringMultiple(new Func<Void, Iterable<HandlerRegistration>>() {
            @Override public Iterable<HandlerRegistration> apply(Void arg) {
                return that.setEditModeToolFrameRegistrations(toolFrame);
            }});
    }

    private void startDraggingFloatingWidget(final ToolboxItem toolboxItem) {
        final WorksheetViewImpl that = this;
        final CanvasToolFactory<? extends CanvasTool<? extends ElementData>> toolFactory = toolboxItem.getToolFactory();
        final Point2D floatingWidgetCreationOffset = toolFactory.getFloatingWidgetCreationOffset();
        MouseMoveOperationHandler handler = new MouseMoveOperationHandler() {
            @Override public void onStop(Point2D pos) {
                Point2D snappedPos = this.calcTargetPos(pos);
                Point2D creationPos = that.translateDragPanelPosToToolContainerPos(snappedPos);
                _toolCreationRequestEvent.dispatch(new ToolCreationRequest(creationPos, toolFactory));
            }

            @Override public void onStart() { }

            @Override public void onMouseMove(Point2D pos) {
                ElementUtils.setElementCSSPosition(that._floatingWidget.getElement(), 
                                                   this.calcTargetPos(pos).plus(floatingWidgetCreationOffset));
            }

            private Point2D calcTargetPos(Point2D pos)
            {
                return that._toolFrameTransformer.applySnapToGrid(pos);
            }

            @Override public void onCancel() { }
        };
        this._floatingWidgetTerminated.addHandler(this._floatingWidgetDragManager.startMouseMoveOperation(
                null, this.dragPanel.getElement(), Point2D.zero,
                handler, ElementDragManager.StopCondition.STOP_CONDITION_MOVEMENT_STOP));
    }

    private void startDraggingSelectedToolFrames()
    {
        _toolFrameTransformer.startDragCanvasToolFrames(IterableUtils.<CanvasToolFrame, CanvasToolFrame>upCast(_selectedTools));
    }

    private void dispatchToolCreationWithoutFloatingWidget(final ToolboxItem toolboxItem, final HumanInputEvent<?> event)
    {
        Point2D position = ElementUtils.getMousePositionRelativeToElement(toolFramesContainer.getElement());
        _toolCreationRequestEvent.dispatch(new ToolCreationRequest(position, toolboxItem.getToolFactory()) {
            @Override public void toolCreated(CanvasTool<? extends ElementData> tool) {
                super.toolCreated(tool);
                tool.asWidget().fireEvent(event);
            }
        });
    }

    private Point2D translateDragPanelPosToToolContainerPos(Point2D toolFramePos) 
    {
        // Transform the coordinates for creating a tool frame from a floating widget
        // transforms them from the drag panel coordinates to the toolsContainerPanel coordinates
        return toolFramePos.minus(ElementUtils.getElementOffsetPosition(this.toolFramesContainer.getElement()))
                           .plus(ElementUtils.getElementOffsetPosition(this.dragPanel.getElement()));
    }

    private ArrayList<HandlerRegistration> setEditModeToolFrameRegistrations(final CanvasToolFrame toolFrame) 
    {
        ArrayList<HandlerRegistration> res = ArrayUtils.toList(new HandlerRegistration[] {
                toolFrame.addMoveStartRequestHandler(new SimpleEvent.Handler<Void>() {
                    @Override public void onFire(Void arg) {
                        _toolFrameSelectionManager.forceToolFrameSelection(toolFrame);
                        startDraggingSelectedToolFrames();
                    }
                }),
                toolFrame.addResizeStartRequestHandler(new SimpleEvent.Handler<Void>() {
                    @Override public void onFire(Void arg) {
                        _toolFrameSelectionManager.forceToolFrameSelection(toolFrame);
                        _toolFrameTransformer.startResizeCanvasToolFrame(toolFrame);
                    }
                }),
                toolFrame.addFocusHandler(new FocusHandler() {
                    @Override public void onFocus(FocusEvent event) {
                        _activeToolFrameChangedEvent.dispatch(toolFrame);
                    }
                }),
                toolFrame.addMouseUpHandler(new MouseUpHandler() {
                    @Override public void onMouseUp(MouseUpEvent event) {
                        _toolFrameSelectionManager.handleToolFrameSelection(toolFrame);
                    }
                }),
        });
        if (toolFrame.getTool().canRotate()) {
            res.add(toolFrame.addRotateStartRequestHandler(new SimpleEvent.Handler<Void>() {
                @Override public void onFire(Void arg) {
                    _toolFrameSelectionManager.forceToolFrameSelection(toolFrame);
                    _toolFrameTransformer.startRotateCanvasToolFrame(toolFrame);
                }
            }));
        }
        return res;
    }

}
