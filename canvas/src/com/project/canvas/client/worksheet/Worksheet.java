package com.project.canvas.client.worksheet;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.project.canvas.client.canvastools.Image.ImageToolFactory;
import com.project.canvas.client.canvastools.TaskList.TaskListToolFactory;
import com.project.canvas.client.canvastools.TextEdit.TextEditToolFactory;
import com.project.canvas.client.canvastools.base.CanvasTool;
import com.project.canvas.client.canvastools.base.CanvasToolFactory;
import com.project.canvas.client.canvastools.base.CanvasToolFrame;
import com.project.canvas.client.canvastools.base.ToolboxItem;
import com.project.canvas.client.resources.CanvasResources;
import com.project.canvas.client.resources.MainStyles;
import com.project.canvas.client.shared.NativeUtils;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.shared.contracts.CanvasService;
import com.project.canvas.shared.contracts.CanvasServiceAsync;
import com.project.canvas.shared.data.CanvasPage;
import com.project.canvas.shared.data.CanvasPageOptions;
import com.project.canvas.shared.data.ElementData;
import com.project.canvas.shared.data.ImageData;
import com.project.canvas.shared.data.Point2D;
import com.project.canvas.shared.data.TaskListData;
import com.project.canvas.shared.data.TextData;

public class Worksheet extends Composite {

	private static WorksheetUiBinder uiBinder = GWT.create(WorksheetUiBinder.class);

	interface WorksheetUiBinder extends UiBinder<Widget, Worksheet> {
	}

	@UiField
	FocusPanel focusPanelDummy;
	
	@UiField
	FlowPanel worksheetPanel;

	@UiField
	Button saveButton;

	@UiField
	TextBox loadIdBox;
	
	@UiField
	Button loadButton;
	
	@UiField
	Button viewButton;
	
	@UiField
	HTMLPanel worksheetContainer;
	
	@UiField
	HTMLPanel worksheetHeader;
	
	@UiField
	Anchor optionsBackground;
	
	@UiField
	FlowPanel worksheetBackground;
	
	ToolboxItem activeToolboxItem;
	Widget activeToolFloatingWidget;
	
	protected final WorksheetOptionsWidget optionsWidget = new WorksheetOptionsWidget();
	protected final DialogBox optionsDialog = new DialogBox(false, true);

	public final SimpleEvent<Void> defaultToolRequestEvent = new SimpleEvent<Void>();
	public final SimpleEvent<Boolean> viewModeEvent = new SimpleEvent<Boolean>();
	
	public SimpleEvent<Boolean> getViewModeEvent() {
		return viewModeEvent;
	}

	public SimpleEvent<Void> getDefaultToolRequestEvent() {
		return defaultToolRequestEvent;
	}

	private class ToolInstanceInfo {
		public ToolInstanceInfo(CanvasToolFactory<?> factory, CanvasToolFrame toolFrame, HandlerRegistration killRegistration) {
			super();
			this.factory = factory;
			this.killRegistration = killRegistration;
			this.createdOn = new Date();
			this.toolFrame = toolFrame;
		}
		CanvasToolFactory<?> factory;
		CanvasToolFrame toolFrame;
		HandlerRegistration killRegistration;
		Date createdOn;
	}
	final HashMap<CanvasTool<? extends ElementData>, ToolInstanceInfo> toolRegsMap = new HashMap<CanvasTool<? extends ElementData>, ToolInstanceInfo>();

	protected CanvasPage page = new CanvasPage();

	private HandlerRegistration workSheetClickHandler;

	private HandlerRegistration workSheetMouseOverHandler;

	private HandlerRegistration workSheetMouseMoveHandler;
	
	public Worksheet() {
		initWidget(uiBinder.createAndBindUi(this));
		optionsDialog.setText("Worksheet options");
		optionsDialog.add(this.optionsWidget);
		setRegistrations();
	}

	private void setRegistrations() {
		this.saveButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				save();
			}
		});
		this.loadButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				loadClicked();
			}
		});
		final Worksheet that = this;
		this.viewButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				viewModeEvent.dispatch(true);
				worksheetHeader.addStyleName(CanvasResources.INSTANCE.main().displayNone());
				that.addStyleName(CanvasResources.INSTANCE.main().worksheetFullView());
			}
		});
		this.optionsBackground.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				that.showOptionsDialog();
			}
		});
		this.optionsWidget.cancelEvent.addHandler(new  SimpleEvent.Handler<Void>(){
			@Override
			public void onFire(Void arg) {
				optionsDialog.hide();
			}});
		this.optionsWidget.doneEvent.addHandler(new  SimpleEvent.Handler<Void>(){

			@Override
			public void onFire(Void arg) {
				optionsDialog.hide();
				that.updateOptions(optionsWidget.getValue());
			}});
	}

	protected void updateOptions(CanvasPageOptions value) {
		this.page.options = value;
		Style style = this.worksheetBackground.getElement().getStyle(); 
		if (value.backgroundImageURL == null || value.backgroundImageURL.trim().isEmpty()) {
			style.setBackgroundImage("");
		}
		else {
			style.setBackgroundImage("url(" + value.backgroundImageURL + ")");
		}
		style.setProperty("backgroundRepeat", value.backgroundRepeat);
		style.setProperty("backgroundSize", value.backgroundSize);
		style.setProperty("backgroundPosition", value.backgroundPosition);
	}

	protected void showOptionsDialog() {
		optionsWidget.setValue(this.page.options);
		optionsDialog.center();
	}

	protected void workSheetClicked(ClickEvent event) {
		//This event is registered only if the tool has a valid ToolFactory so there's no need to check 
		//for null.
		focusPanelDummy.setFocus(true);
		CanvasToolFactory<? extends CanvasTool<? extends ElementData>> toolFactory = 
			this.activeToolboxItem.getToolFactory();
		Point2D pos = relativePosition(event, this.worksheetPanel.getElement());
		createToolInstance(pos, toolFactory);
		if (toolFactory.isOneShot()) {
			defaultToolRequestEvent.dispatch(null);
		}
	}

	protected Point2D relativePosition(MouseEvent<?> event, Element elem) {
		int x = event.getRelativeX(elem);
		int y = event.getRelativeY(elem);
		Point2D pos = new Point2D(x,y);
		return pos;
	}

	private CanvasTool<? extends ElementData> createToolInstance(Point2D relativePos, CanvasToolFactory<? extends CanvasTool<? extends ElementData>> toolFactory) {
		CanvasTool<? extends ElementData> tool = toolFactory.create();
		final CanvasToolFrame toolFrame = new CanvasToolFrame(tool);

		toolFrame.getCloseRequest().addHandler(new SimpleEvent.Handler<Void>() {
			@Override
			public void onFire(Void arg) {
				removeToolInstance(toolFrame);
			}
		});
		toolFrame.getMoveStartRequest().addHandler(new SimpleEvent.Handler<MouseDownEvent>() {
			@Override
			public void onFire(MouseDownEvent arg) {
				startDragCanvasToolFrame(toolFrame, arg);
			}
		});
		setToolFramePosition(relativePos, toolFrame);
		
		this.worksheetPanel.add(toolFrame);
		HandlerRegistration reg = tool.getKillRequestedEvent().addHandler(new SimpleEvent.Handler<String>() {
			public void onFire(String arg) {
				removeToolInstance(toolFrame);
			}
		});
		this.toolRegsMap.put(tool, new ToolInstanceInfo(toolFactory, toolFrame, reg));
		tool.setFocus(true);
		return tool;
	}

	protected void setToolFramePosition(Point2D relativePos, final CanvasToolFrame toolFrame) {
		toolFrame.asWidget().getElement().getStyle().setLeft(relativePos.getX(), Unit.PX);
		toolFrame.asWidget().getElement().getStyle().setTop(relativePos.getY(), Unit.PX);
	}

	protected void startDragCanvasToolFrame(final CanvasToolFrame toolFrame, final MouseEvent<?> startEvent) 
	{
		final Point2D toolFrameOffset = relativePosition(startEvent, toolFrame.getElement());
		final ArrayList<HandlerRegistration> regs = new ArrayList<HandlerRegistration>();
		NativeUtils.disableTextSelectInternal(this.worksheetPanel.getElement(), true);
		regs.add(this.worksheetPanel.addDomHandler(new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				Point2D pos = relativePosition(event, worksheetPanel.getElement());
				pos.setX(pos.getX() - toolFrameOffset.getX());
				pos.setY(pos.getY() - toolFrameOffset.getY());
				setToolFramePosition(limitPosToWorksheet(pos, toolFrame), toolFrame);
			}
		}, MouseMoveEvent.getType()));
		regs.add(this.worksheetPanel.addDomHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				for (HandlerRegistration reg : regs) {
					NativeUtils.disableTextSelectInternal(worksheetPanel.getElement(), false);
					reg.removeHandler();
				}
			}}, MouseUpEvent.getType()));
	}

	protected Point2D limitPosToWorksheet(Point2D pos, Widget elem) {
		Point2D result = new Point2D();
		int maxX = this.worksheetPanel.getOffsetWidth() - elem.getOffsetWidth();
		int maxY = this.worksheetPanel.getOffsetHeight() - elem.getOffsetHeight();
		result.setX(Math.min(maxX, Math.max(0, pos.getX())));
		result.setY(Math.min(maxY, Math.max(0, pos.getY())));
		return result;
	}

	protected void removeToolInstance(CanvasToolFrame toolFrame) {
		ToolInstanceInfo info = this.toolRegsMap.remove(toolFrame.getTool());
		this.worksheetPanel.remove(toolFrame);
		info.killRegistration.removeHandler();
	}

	public void setActiveTool(ToolboxItem toolboxItem) {
		this.clearActiveTool();
		
		this.activeToolboxItem = toolboxItem;
		this.worksheetPanel.addStyleName(toolboxItem.getCanvasStyleInCreateMode());
		
		this.registerToolHandlers(this.activeToolboxItem);
	}
	
	protected void clearActiveTool()
	{
		if (null != this.activeToolboxItem)
		{
			this.worksheetPanel.removeStyleName(this.activeToolboxItem.getCanvasStyleInCreateMode());
			this.activeToolboxItem = null;
		}
		if (null != this.activeToolFloatingWidget)
		{
			this.worksheetPanel.remove(this.activeToolFloatingWidget);
			this.activeToolFloatingWidget = null;
		}
		if (null != this.workSheetClickHandler)
		{
			this.workSheetClickHandler.removeHandler();
			this.workSheetClickHandler = null;
		}
		if (null != this.workSheetMouseOverHandler)
		{
			this.workSheetMouseOverHandler.removeHandler();
			this.workSheetMouseOverHandler = null;
		}
		if (null != this.workSheetMouseMoveHandler)
		{
			this.workSheetMouseMoveHandler.removeHandler();
			this.workSheetMouseMoveHandler = null;
		}
	}
	
	protected void registerToolHandlers(ToolboxItem toolboxItem)
	{
		CanvasToolFactory<? extends CanvasTool<? extends ElementData>> toolFactory = 
			this.activeToolboxItem.getToolFactory();
		if (null == toolFactory)
		{
			return;
		}
		this.workSheetClickHandler = this.worksheetPanel.addDomHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				workSheetClicked(event);
			}}, ClickEvent.getType());
		
		Widget floatingWidget = toolFactory.getFloatingWidget();
		if (null == floatingWidget)
		{
			return;
		}
		this.workSheetMouseOverHandler = this.worksheetPanel.addDomHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				workSheetMouseOver(event);
			}}, MouseOverEvent.getType());
	}
	
	protected void workSheetMouseOver(MouseOverEvent event)
	{
		if (null != this.activeToolFloatingWidget)
		{
			return;
		}
		this.activeToolFloatingWidget = this.activeToolboxItem.getToolFactory().getFloatingWidget();
		if (null == this.activeToolFloatingWidget)
		{
			return;
		}
		this.activeToolFloatingWidget.addStyleName(CanvasResources.INSTANCE.main().floatingToolStyle());
		this.worksheetPanel.add(this.activeToolFloatingWidget);
		this.workSheetMouseMoveHandler = this.worksheetPanel.addDomHandler(new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				workSheetMouseMove(event);
			}}, MouseMoveEvent.getType());
	}
	
	protected void workSheetMouseMove(MouseMoveEvent event)
	{
		this.activeToolFloatingWidget.getElement().getStyle().setTop(
				event.getRelativeY(worksheetPanel.getElement()), Unit.PX);
		this.activeToolFloatingWidget.getElement().getStyle().setLeft(
				event.getRelativeX(worksheetPanel.getElement()), Unit.PX);
	}
	
	protected void save() {
		ArrayList<ElementData> activeElems = new ArrayList<ElementData>();
		for (Entry<CanvasTool<? extends ElementData>, ToolInstanceInfo>  entry : toolRegsMap.entrySet())
		{
			CanvasTool<? extends ElementData> tool = entry.getKey();
			ToolInstanceInfo toolInfo = entry.getValue();
			ElementData toolData = tool.getValue();
			int x = Integer.valueOf(toolInfo.toolFrame.getElement().getOffsetLeft());
			int y = Integer.valueOf(toolInfo.toolFrame.getElement().getOffsetTop());
			toolData.position = new Point2D(x, y);
			activeElems.add(toolData);
		}
		this.page.elements.clear();
		this.page.elements.addAll(activeElems);
		
		CanvasServiceAsync service = (CanvasServiceAsync)GWT.create(CanvasService.class);
		
		this.saveButton.setText("Saving...");
		this.saveButton.setEnabled(false);
		
		Window.setStatus("Saving...");
		service.SavePage(page, new AsyncCallback<CanvasPage>() {
			@Override
			public void onSuccess(CanvasPage result) {
				saveButton.setEnabled(true);
				saveButton.setText("Save");
				String newURL = Window.Location.createUrlBuilder().setHash(result.id.toString()).buildString();
				Window.Location.replace(newURL);
				Window.setStatus("");
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Save failed. Reason: " + caught.toString());
				saveButton.setEnabled(true);
				saveButton.setText("Save");
				Window.setStatus("");
			}
		});
	}
	
	protected void load(Long id) {
		CanvasServiceAsync service = (CanvasServiceAsync)GWT.create(CanvasService.class);
		
		this.loadButton.setText("Loading...");
		this.loadButton.setEnabled(false);
	
		service.GetPage(id, new AsyncCallback<CanvasPage>() {
			@Override
			public void onSuccess(CanvasPage result) {
				loadButton.setEnabled(true);
				loadButton.setText("Load");
				if (null != result) {
					String newURL = Window.Location.createUrlBuilder().setHash(result.id.toString()).buildString();
					Window.Location.replace(newURL);
					load(result);
				}
				else {
					Window.alert("No such page on server.");
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Load failed. Reason: " + caught.toString());
				loadButton.setEnabled(true);
				loadButton.setText("Load");
			}
		});
	}

	protected void load(CanvasPage result) {
		this.page = result;
		this.updateOptions(result.options);
		HashMap<Long, ElementData> updatedElements = new HashMap<Long, ElementData>();
		for (ElementData elem : this.page.elements) {
			updatedElements.put(elem.id, elem);
		}
		
		for (Entry<CanvasTool<? extends ElementData>, ToolInstanceInfo>  entry 
				: new HashSet<Entry<CanvasTool<? extends ElementData>, ToolInstanceInfo>>(toolRegsMap.entrySet()))
		{
			CanvasTool<? extends ElementData> tool = entry.getKey();
			ToolInstanceInfo toolInfo = entry.getValue();
			ElementData toolData = tool.getValue();
			if (updatedElements.containsKey(toolData.id)) {
				tool.setElementData(updatedElements.get(toolData.id));
				updatedElements.remove(toolData.id);
			}
			else {
				this.removeToolInstance(toolInfo.toolFrame);
			}
		}
		
		createToolInstancesFromData(updatedElements);
	}

	private void createToolInstancesFromData(HashMap<Long, ElementData> updatedElements) {
		for (ElementData newElement : updatedElements.values()) {
			CanvasTool<? extends ElementData> tool = null;
			CanvasToolFactory<? extends CanvasTool<? extends ElementData>> factory = null;
			Class<?> cls = newElement.getClass();
			if (cls.equals(TextData.class)) {
				factory = new TextEditToolFactory();
			}
			else if (cls.equals(TaskListData.class)) {
				factory = new TaskListToolFactory();
			}
			else if (cls.equals(ImageData.class)) {
				factory = new ImageToolFactory();
			}
			
			if (null == factory) {
				continue;
			}
			tool = this.createToolInstance(newElement.position, factory);
			tool.setElementData(newElement);
			tool.setFocus(false);
		}
	}

	private void loadClicked() {
		String idStr = loadIdBox.getText();
		load(idStr);
	}

	public void load(String idStr) {
		Long id = null;
		try {
			id = Long.valueOf(idStr);
		}
		catch (NumberFormatException e) {
			return;
		}
		if (null != id) {
			load(id);
		}
	}
}
