package com.project.canvas.client.worksheet;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseEvent;
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
import com.project.canvas.client.shared.DialogWithZIndex;
import com.project.canvas.client.shared.ElementUtils;
import com.project.canvas.client.shared.RegistrationsManager;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.client.shared.events.SimpleEvent.Handler;
import com.project.canvas.shared.data.CanvasPageOptions;
import com.project.canvas.shared.data.ElementData;
import com.project.canvas.shared.data.Point2D;
import com.project.canvas.shared.data.Transform2D;

public class WorksheetViewImpl extends Composite implements WorksheetView
{
    interface WorksheetViewImplUiBinder extends UiBinder<Widget, WorksheetViewImpl>
    {
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


    private RegistrationsManager _floatingWidgetRegistrations;
    private Widget floatingWidget;
    private ToolboxItem activeToolboxItem;
    private CanvasPageOptions pageOptions;
    
    private final SimpleEvent<CanvasPageOptions> optionsUpdatedEvent = new SimpleEvent<CanvasPageOptions>();
    private final SimpleEvent<Void> stopOperationEvent = new SimpleEvent<Void>();
    private final SimpleEvent<ToolCreationRequest> toolCreationRequestEvent = new SimpleEvent<ToolCreationRequest>();
    
    private final DialogBox optionsDialog = new DialogWithZIndex(false, true);
    private final WorksheetOptionsWidget optionsWidget = new WorksheetOptionsWidget();
    
    private final HashMap<Widget, RegistrationsManager> toolFrameRegistrations = new HashMap<Widget, RegistrationsManager>();
    
    private final ToolFrameTransformer _toolFrameTransformer;
    
    public WorksheetViewImpl()
    {
        initWidget(uiBinder.createAndBindUi(this));
        _toolFrameTransformer  = new ToolFrameTransformer(worksheetPanel, dragPanel, stopOperationEvent);
        optionsDialog.setText("Worksheet options");
        optionsDialog.add(this.optionsWidget);
        this.dragPanel.setVisible(false);
        this.addRegistrations();
    }

    @Override
    public HandlerRegistration addLoadHandler(final Handler<String> handler)
    {
        return loadButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event)
            {
                load(handler);
            }
        });
    }

    @Override
    public HandlerRegistration addOptionsUpdatedHandler(Handler<CanvasPageOptions> handler)
    {
        return optionsUpdatedEvent.addHandler(handler);
    }

    @Override
    public HandlerRegistration addSaveHandler(Handler<Void> handler)
    {
        return saveButton.addClickHandler(SimpleEvent.AsClickHandler(handler));
    }

    @Override
    public HandlerRegistration addStopOperationHandler(Handler<Void> handler)
    {
        return stopOperationEvent.addHandler(handler);
    }

    @Override
    public HandlerRegistration addToolCreationRequestHandler(Handler<ToolCreationRequest> handler)
    {
        return toolCreationRequestEvent.addHandler(handler);
    }

    @Override
    public void addToolInstanceWidget(final CanvasToolFrame toolFrame, final Transform2D transform, final Point2D additionalOffset)
    {
        RegistrationsManager regs = new RegistrationsManager();
        toolFrameRegistrations.put(toolFrame, regs);
        
        regs.add(toolFrame.addAttachHandler(new AttachEvent.Handler() {
            @Override
            public void onAttachOrDetach(AttachEvent event)
            {
                if (event.isAttached()) {
                    _toolFrameTransformer.setToolFramePosition(toolFrame, transform.translation.plus(additionalOffset));
                    ElementUtils.setRotation(toolFrame.getElement(), transform.rotation);
                    if (null != transform.size) {
                        toolFrame.setToolSize(transform.size);

                    }
                }
            }
        }));
        
        regs.add(toolFrame.getMoveStartRequest().addHandler(new SimpleEvent.Handler<MouseEvent<?>>() {
            @Override
            public void onFire(MouseEvent<?> arg) {
                _toolFrameTransformer.startDragCanvasToolFrame(toolFrame, arg);
            }
        }));
        regs.add(toolFrame.addResizeStartRequestHandler(new SimpleEvent.Handler<MouseEvent<?>>() {
            @Override
            public void onFire(MouseEvent<?> arg) {
                _toolFrameTransformer.startResizeCanvasToolFrame(toolFrame, arg);
            }
        }));
        regs.add(toolFrame.addRotateStartRequestHandler(new SimpleEvent.Handler<MouseEvent<?>>() {
            @Override
            public void onFire(MouseEvent<?> arg) {
                _toolFrameTransformer.startRotateCanvasToolFrame(toolFrame, arg);
            }
        }));
        
        this.worksheetPanel.add(toolFrame);
    }

    @Override
    public HandlerRegistration addViewHandler(Handler<Void> handler)
    {
        return viewButton.addClickHandler(SimpleEvent.AsClickHandler(handler));
    }

    @Override
    public void clearActiveToolboxItem()
    {
        clearFloatingWidget();
        if (null != this.activeToolboxItem) {
            this.worksheetPanel.removeStyleName(this.activeToolboxItem.getCanvasStyleInCreateMode());
            this.activeToolboxItem = null;
        }
    }

    public void load(Handler<String> handler)
    {
        String idStr = loadIdBox.getText();
        handler.onFire(idStr);
    }

    @Override
    public void onLoadOperationChange(OperationStatus status, String reason)
    {
        this.changeButtonStatus(loadButton, status, "Loading...", "Load");
        if (OperationStatus.FAILURE == status) {
            Window.alert("Load failed. Reason: " + reason);
        }
    }

    @Override
    public void onSaveOperationChange(OperationStatus status, String reason)
    {
        this.changeButtonStatus(saveButton, status, "Saving...", "Save");
        if (OperationStatus.FAILURE == status) {
            Window.alert("Save failed. Reason: " + reason);
        }
    }

    protected void clearFloatingWidget()
    {
        if (null != this.floatingWidget){ 
            this.worksheetPanel.remove(floatingWidget);
        }
        if (null != this._floatingWidgetRegistrations) {
            this._floatingWidgetRegistrations.clear();
        }
        this.floatingWidget = null;
        this._floatingWidgetRegistrations = null;
    }

    @Override
    public void removeToolInstanceWidget(CanvasToolFrame widget)
    {
        this.worksheetPanel.remove(widget);
        RegistrationsManager regs = toolFrameRegistrations.remove(widget);
        if (null != regs) {
            regs.clear();
        }
    }

    @Override
	public void setActiveToolboxItem(final ToolboxItem toolboxItem) {
	    this.activeToolboxItem = toolboxItem;
        clearFloatingWidget();
        this.worksheetPanel.addStyleName(toolboxItem.getCanvasStyleInCreateMode());
		this.worksheetPanel.addDomHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                Point2D pos = ElementUtils.relativePosition(event, worksheetPanel.getElement());
            	toolCreationRequestEvent.dispatch(new ToolCreationRequest(pos, toolboxItem.getToolFactory()));
            }
        }, ClickEvent.getType());
		
		CanvasToolFactory<? extends CanvasTool<? extends ElementData>> factory = toolboxItem.getToolFactory();
		if (null == factory) {
		    return;
		}
		
		final Widget floatingWidget = factory.getFloatingWidget();
		if (null == floatingWidget) {
		    return;
		}
        floatingWidget.addStyleName(CanvasResources.INSTANCE.main().floatingToolStyle());
        this.worksheetPanel.add(floatingWidget);

		Handler<Point2D> floatingWidgetMoveHandler = new Handler<Point2D>() {
            @Override
            public void onFire(Point2D arg)
            {
                ElementUtils.setElementPosition(arg, floatingWidget.getElement());
            }
        };
        Handler<Point2D> floatingWidgetStop = new Handler<Point2D>() {
            @Override
            public void onFire(Point2D position)
            {
                toolCreationRequestEvent.dispatch(new ToolCreationRequest(position, toolboxItem.getToolFactory()));
            }
        };
        this.floatingWidget = floatingWidget;
        this._floatingWidgetRegistrations = this._toolFrameTransformer.getElementDragManager()
		    .startMouseMoveOperation(this.worksheetPanel.getElement(), Point2D.zero, 
		            floatingWidgetMoveHandler, floatingWidgetStop, null, 
		            ElementDragManager.StopCondition.STOP_CONDITION_MOUSE_CLICK);
	}

    @Override
    public void setOptions(CanvasPageOptions value)
    {
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
    public void setViewMode(boolean isViewMode)
    {
        if (isViewMode) {
            worksheetHeader.addStyleName(CanvasResources.INSTANCE.main().displayNone());
            addStyleName(CanvasResources.INSTANCE.main().worksheetFullView());
        } else {
            worksheetHeader.removeStyleName(CanvasResources.INSTANCE.main().displayNone());
            removeStyleName(CanvasResources.INSTANCE.main().worksheetFullView());
        }
    }

    private void addRegistrations()
    {
        this.optionsBackground.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event)
            {
                optionsDialog.center();
            }
        });
        this.optionsWidget.cancelEvent.addHandler(new SimpleEvent.Handler<Void>() {
            @Override
            public void onFire(Void arg)
            {
                optionsDialog.hide();
            }
        });
        this.optionsWidget.doneEvent.addHandler(new SimpleEvent.Handler<Void>() {

            @Override
            public void onFire(Void arg)
            {
                optionsDialog.hide();
                optionsUpdatedEvent.dispatch(optionsWidget.getValue());
            }
        });
        Event.addNativePreviewHandler(new NativePreviewHandler() {
            @Override
            public void onPreviewNativeEvent(NativePreviewEvent event)
            {
                String type = event.getNativeEvent().getType();
                if (type.equals("keypress") && (event.getNativeEvent().getKeyCode() == 27)) {
                    stopOperationEvent.dispatch(null);
                }
            }
        });
    }

    private void changeButtonStatus(Button button, OperationStatus status, String pendingText, String doneText)
    {
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
}
