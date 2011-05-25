package com.project.canvas.client.worksheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
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
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.project.canvas.client.canvastools.base.CanvasTool;
import com.project.canvas.client.canvastools.base.CanvasToolFactory;
import com.project.canvas.client.canvastools.base.CanvasToolFrame;
import com.project.canvas.client.canvastools.base.ToolboxItem;
import com.project.canvas.client.resources.CanvasResources;
import com.project.canvas.client.shared.ElementUtils;
import com.project.canvas.client.shared.RegistrationsManager;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.client.shared.events.SimpleEvent.Handler;
import com.project.canvas.client.shared.widgets.DialogWithZIndex;
import com.project.canvas.client.worksheet.interfaces.ElementDragManager;
import com.project.canvas.client.worksheet.interfaces.ToolFrameTransformer;
import com.project.canvas.client.worksheet.interfaces.WorksheetOptionsView;
import com.project.canvas.client.worksheet.interfaces.WorksheetView;
import com.project.canvas.shared.data.CanvasPageOptions;
import com.project.canvas.shared.data.ElementData;
import com.project.canvas.shared.data.Point2D;
import com.project.canvas.shared.data.Transform2D;

public class WorksheetViewImpl extends Composite implements WorksheetView {
    interface WorksheetViewImplUiBinder extends UiBinder<Widget, WorksheetViewImpl> {
    }

    private static WorksheetViewImplUiBinder uiBinder = GWT.create(WorksheetViewImplUiBinder.class);

    @UiField
    HTMLPanel dragPanel;
    @UiField
    Button loadButton;
    @UiField
    TextBox loadIdBox;

    @UiField
    Anchor optionsBackground;
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
    FlowPanel worksheetPanel;

    private Handler<Void> _floatingWidgetTerminator;
    private ToolboxItem activeToolboxItem;
    private Widget floatingWidget;
    private CanvasPageOptions pageOptions;

    private final ToolFrameTransformer _toolFrameTransformer;

    private final DialogBox optionsDialog = new DialogWithZIndex(false, true);
    private final WorksheetOptionsView optionsWidget = new WorksheetOptionsViewImpl();
    private final HashMap<CanvasToolFrame, RegistrationsManager> toolFrameRegistrations = new HashMap<CanvasToolFrame, RegistrationsManager>();

    private final HashSet<CanvasToolFrame> overToolFrames = new HashSet<CanvasToolFrame>();

    private final SimpleEvent<CanvasPageOptions> optionsUpdatedEvent = new SimpleEvent<CanvasPageOptions>();
    private final SimpleEvent<Void> stopOperationEvent = new SimpleEvent<Void>();
    private final SimpleEvent<ToolCreationRequest> toolCreationRequestEvent = new SimpleEvent<ToolCreationRequest>();
    private final SimpleEvent<CanvasToolFrame> toolFrameClickEvent = new SimpleEvent<CanvasToolFrame>();
    private final SimpleEvent<MouseEvent<?>> mouseDownEvent = new SimpleEvent<MouseEvent<?>>();

    private HashSet<CanvasToolFrame> selectedTools = new HashSet<CanvasToolFrame>();
    
	private boolean viewMode;
	private boolean viewModeSet = false;

    public WorksheetViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
        _toolFrameTransformer = new ToolFrameTransformerImpl(worksheetPanel, dragPanel, stopOperationEvent);
        optionsDialog.setText("Worksheet options");
        optionsDialog.add(this.optionsWidget);
        this.dragPanel.setVisible(false);
        this.addRegistrations();
        this.setViewMode(false);
    }

    @Override
    public HandlerRegistration addLoadHandler(final Handler<String> handler) {
        return loadButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                load(handler);
            }
        });
    }

    @Override
    public HandlerRegistration addOptionsUpdatedHandler(Handler<CanvasPageOptions> handler) {
        return optionsUpdatedEvent.addHandler(handler);
    }

    @Override
    public HandlerRegistration addSaveHandler(Handler<Void> handler) {
        return saveButton.addClickHandler(SimpleEvent.AsClickHandler(handler));
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
    
    public HandlerRegistration attMouseDownHandler(Handler<MouseEvent<?>> handler)
    {
        return this.mouseDownEvent.addHandler(handler);
    }

    @Override
    public void addToolInstanceWidget(final CanvasToolFrame toolFrame, final Transform2D transform,
            final Point2D additionalOffset)
    {
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
                if (false == isToolFrameSelected(toolFrame))
                {
                    handleToolFrameSelection(toolFrame);
                }
                for (CanvasToolFrame selectedToolFrame : selectedTools){
                    _toolFrameTransformer.startDragCanvasToolFrame(selectedToolFrame, arg);
                }
            }
        }));
        regs.add(toolFrame.addResizeStartRequestHandler(new SimpleEvent.Handler<MouseEvent<?>>() {
            @Override
            public void onFire(MouseEvent<?> arg) {
                for (CanvasToolFrame selectedToolFrame : selectedTools){
                    _toolFrameTransformer.startResizeCanvasToolFrame(selectedToolFrame, arg);
                }
            }
        }));
        if (toolFrame.getTool().canRotate()) {
            regs.add(toolFrame.addRotateStartRequestHandler(new SimpleEvent.Handler<MouseEvent<?>>() {
                @Override
                public void onFire(MouseEvent<?> arg) {
                    for (CanvasToolFrame selectedToolFrame : selectedTools){
                        _toolFrameTransformer.startRotateCanvasToolFrame(selectedToolFrame, arg);
                    }
                }
            }));
        }
        regs.add(toolFrame.addFocusHandler(new FocusHandler() {
            @Override
            public void onFocus(FocusEvent event) {
                onToolFrameClick(toolFrame);
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
//        regs.add(toolFrame.addSelectRequestHandler(new SimpleEvent.Handler<Void>() {
//            @Override
//            public void onFire(Void arg) {
//                onFrameSelected(toolFrame);
//            }
//        }));

        this.worksheetPanel.add(toolFrame);
    }
    
    private void onToolFrameClick(CanvasToolFrame toolFrame)
    {
        this.handleToolFrameSelection(toolFrame);
        toolFrameClickEvent.dispatch(toolFrame);
    }
    
    private void handleToolFrameSelection(CanvasToolFrame toolFrame)
    {
        Event event = Event.getCurrentEvent();
        if ((null != event) && (event.getCtrlKey()))
        {
            this.toggleToolFrameSelection(toolFrame);
        }
        else
        {
            this.clearFrameSelection();
            this.selectToolFrame(toolFrame);
        }        
    }
    
    private void selectToolFrame(CanvasToolFrame toolFrame)
    {
        this.selectedTools.add(toolFrame);
        toolFrame.addStyleName(CanvasResources.INSTANCE.main().toolFrameSelected());
    }
    
    private void unSelectToolFrame(CanvasToolFrame toolFrame)
    {
        this.selectedTools.remove(toolFrame);
        toolFrame.removeStyleName(CanvasResources.INSTANCE.main().toolFrameSelected());
    }
    
    private boolean isToolFrameSelected(CanvasToolFrame toolFrame)
    {
        return this.selectedTools.contains(toolFrame);
    }
    
    private void clearFrameSelection()
    {
        ArrayList<CanvasToolFrame> framesToClear = new ArrayList<CanvasToolFrame>(this.selectedTools);
        for (CanvasToolFrame toolFrame : framesToClear)
        {
            this.unSelectToolFrame(toolFrame);
        }
    }
    
    private void toggleToolFrameSelection(CanvasToolFrame toolFrame)
    {
        if (this.isToolFrameSelected(toolFrame))
        {
            this.unSelectToolFrame(toolFrame);
        }
        else
        {
            this.selectToolFrame(toolFrame);
        }
    }

    @Override
    public HandlerRegistration addViewHandler(Handler<Void> handler) {
        return viewButton.addClickHandler(SimpleEvent.AsClickHandler(handler));
    }

    @Override
    public void clearActiveToolboxItem() {
        clearFloatingWidget();
        if (null != this.activeToolboxItem) {
            this.worksheetPanel.removeStyleName(this.activeToolboxItem.getCanvasStyleInCreateMode());
            this.activeToolboxItem = null;
        }
    }

    public void load(Handler<String> handler) {
        String idStr = loadIdBox.getText();
        handler.onFire(idStr);
    }

    @Override
    public void onLoadOperationChange(OperationStatus status, String reason) {
        this.changeButtonStatus(loadButton, status, "Loading...", "Load");
        if (OperationStatus.FAILURE == status) {
            Window.alert("Load failed. Reason: " + reason);
        }
    }

    @Override
    public void onSaveOperationChange(OperationStatus status, String reason) {
        this.changeButtonStatus(saveButton, status, "Saving...", "Save");
        if (OperationStatus.FAILURE == status) {
            Window.alert("Save failed. Reason: " + reason);
        }
    }

    @Override
    public void removeToolInstanceWidget(CanvasToolFrame toolFrame) {
        this.worksheetPanel.remove(toolFrame);
        this.overToolFrames.remove(toolFrame);
        RegistrationsManager regs = toolFrameRegistrations.remove(toolFrame);
        if (null != regs) {
            regs.clear();
        }
    }

    @Override
    public void setActiveToolboxItem(final ToolboxItem toolboxItem) {
        if (toolboxItem == this.activeToolboxItem) {
            return;
        }
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
    public void setOptions(CanvasPageOptions value) {
        this.pageOptions = value;
        optionsWidget.setValue(this.pageOptions);
        Style style = this.worksheetBackground.getElement().getStyle();
        if (value.backgroundImageURL == null || value.backgroundImageURL.trim().isEmpty()) {
            style.setBackgroundImage("");
        } else {
            style.setBackgroundImage("url(" + value.backgroundImageURL + ")");
        }
        style.setProperty("backgroundRepeat", value.backgroundRepeat);
        style.setProperty("backgroundSize", value.backgroundSize);
        style.setProperty("backgroundPosition", value.backgroundPosition);
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
            worksheetHeader.addStyleName(CanvasResources.INSTANCE.main().displayNone());
            addStyleName(CanvasResources.INSTANCE.main().worksheetFullView());
            addStyleName(CanvasResources.INSTANCE.main().worksheetModeViewOnly());
            removeStyleName(CanvasResources.INSTANCE.main().worksheetModeEditable());
        } else {
            worksheetHeader.removeStyleName(CanvasResources.INSTANCE.main().displayNone());
            removeStyleName(CanvasResources.INSTANCE.main().worksheetFullView());
            removeStyleName(CanvasResources.INSTANCE.main().worksheetModeViewOnly());
            addStyleName(CanvasResources.INSTANCE.main().worksheetModeEditable());
        }
    }

    private void addRegistrations() {
        this.optionsBackground.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                optionsDialog.center();
            }
        });
        this.optionsWidget.addCancelHandler(new SimpleEvent.Handler<Void>() {
            @Override
            public void onFire(Void arg) {
                optionsDialog.hide();
            }
        });
        this.optionsWidget.addDoneHandler(new SimpleEvent.Handler<Void>() {

            @Override
            public void onFire(Void arg) {
                optionsDialog.hide();
                optionsUpdatedEvent.dispatch(optionsWidget.getValue());
            }
        });
        this.worksheetPanel.addDomHandler(new MouseDownHandler() 
        {
            @Override
            public void onMouseDown(MouseDownEvent event)
            {
                if (overToolFrames.isEmpty()) {
                    onClearAreaClicked();
                }
            }
        }, MouseDownEvent.getType());
        Event.addNativePreviewHandler(new NativePreviewHandler() {
            @Override
            public void onPreviewNativeEvent(NativePreviewEvent event) {
                String type = event.getNativeEvent().getType();
                if (type.equals("keydown") && (event.getNativeEvent().getKeyCode() == 27)) {
                    onEscapePressed();
                }
            }
        });
    }
    
    private void onClearAreaClicked()
    {
        toolFrameClickEvent.dispatch(null);
        clearFrameSelection();
    }
        
    private void onEscapePressed()
    {
        stopOperationEvent.dispatch(null);
        this.clearFrameSelection();
    }
    
    private void changeButtonStatus(Button button, OperationStatus status, String pendingText, String doneText) {
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

    private void setActiveToolboxItemWithoutFloatingWidget(final ToolboxItem toolboxItem)
    {
        final HandlerRegistration createInstanceReg = this.worksheetPanel.addDomHandler(
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        if (overToolFrames.isEmpty()) {
                            Point2D position = ElementUtils.relativePosition(event,
                                    worksheetPanel.getElement());
                            toolCreationRequestEvent.dispatch(new ToolCreationRequest(position,
                                    toolboxItem.getToolFactory()));
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

    private void setFloatingWidgetForTool(CanvasToolFactory<? extends CanvasTool<? extends ElementData>> factory)
    {
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
            ElementUtils.setElementPosition(Point2D.max(Point2D.zero, relativeToWorksheet.minus(worksheetPos)), floatingWidget.getElement());
        }
    }

    private void startDraggingFloatingWidget(final ToolboxItem toolboxItem)
    {
        final WorksheetViewImpl that = this;
        Handler<Point2D> floatingWidgetMoveHandler = new Handler<Point2D>() {
            @Override
            public void onFire(Point2D arg) {
                ElementUtils.setElementPosition(arg, that.floatingWidget.getElement());
            }
        };
        Handler<Point2D> floatingWidgetStop = new Handler<Point2D>() {
            @Override
            public void onFire(Point2D position) {
                toolCreationRequestEvent.dispatch(new ToolCreationRequest(position, toolboxItem.getToolFactory()));
            }
        };
        this._floatingWidgetTerminator = this._toolFrameTransformer.getElementDragManager().startMouseMoveOperation(
                this.worksheetPanel.getElement(), Point2D.zero, floatingWidgetMoveHandler, floatingWidgetStop, null,
                ElementDragManager.StopCondition.STOP_CONDITION_MOUSE_CLICK);
    }

    @Override
    public void setToolFrameTransform(final CanvasToolFrame toolFrame, final Transform2D transform,
            final Point2D additionalOffset)
    {
        if (toolFrame.getTool().canRotate()) {
            ElementUtils.setRotation(toolFrame.getElement(), transform.rotation);
        }
        if (null != transform.size) {
            toolFrame.setToolSize(transform.size);
        }
        _toolFrameTransformer.setToolFramePosition(toolFrame, transform.translation.plus(additionalOffset));
    }
}
