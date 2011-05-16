package com.project.canvas.client.worksheet;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.validation.constraints.Max;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
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
import com.project.canvas.client.shared.NativeUtils;
import com.project.canvas.client.shared.RegistrationsManager;
import com.project.canvas.client.shared.ZIndexProvider;
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
	RegistrationsManager activeToolRegistrations = new RegistrationsManager(); 

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
	// Use LinkedHashMap in order to preserver the order of the tools.
	final LinkedHashMap<CanvasTool<? extends ElementData>, ToolInstanceInfo> toolRegsMap = new LinkedHashMap<CanvasTool<? extends ElementData>, ToolInstanceInfo>();

	protected CanvasPage page = new CanvasPage();

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
		if (null == value)
		{
			return;
		}
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
		//Make sure that the dialog is set to the highest ZIndex.
		optionsDialog.getElement().getStyle().setZIndex(ZIndexProvider.getTopMostZIndex());
		optionsDialog.setGlassEnabled(true);
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

	private CanvasToolFrame createToolInstance(final Point2D relativePos, 
			CanvasToolFactory<? extends CanvasTool<? extends ElementData>> toolFactory) 
	{
		return this.createToolInstance(relativePos, 
				ZIndexProvider.allocateZIndex(), toolFactory);
	}
	
	private CanvasToolFrame createToolInstance(final Point2D relativePos, final int zIndex, 
			CanvasToolFactory<? extends CanvasTool<? extends ElementData>> toolFactory) {
		final CanvasTool<? extends ElementData> tool = toolFactory.create();
		final CanvasToolFrame toolFrame = new CanvasToolFrame(tool);
		
		final Point2D creationOffset = toolFactory.getCreationOffset();
		
		//TODO: Remove registrations when tool is killed?
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
		toolFrame.addResizeStartRequestHandler(new SimpleEvent.Handler<MouseDownEvent>() {
			@Override
			public void onFire(MouseDownEvent arg) {
				startResizeCanvasToolFrame(toolFrame, arg);
			}
		});
		toolFrame.addMoveBackRequestHandler(new SimpleEvent.Handler<Void>() {
			@Override
			public void onFire(Void arg) {
				moveToolFrameBack(toolFrame);
			}});
		
		toolFrame.addMoveFrontRequestHandler(new SimpleEvent.Handler<Void>() {
			@Override
			public void onFire(Void arg) {
				moveToolFrameFront(toolFrame);
			}});
		
		this.worksheetPanel.add(toolFrame);
		HandlerRegistration reg = tool.getKillRequestedEvent().addHandler(new SimpleEvent.Handler<String>() {
			public void onFire(String arg) {
				removeToolInstance(toolFrame);
			}
		});
		tool.asWidget().setVisible(false);
		this.toolRegsMap.put(tool, new ToolInstanceInfo(toolFactory, toolFrame, reg));
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				tool.asWidget().setVisible(true);
				tool.setFocus(true);
				setToolFramePosition(limitPosToWorksheet(relativePos.plus(creationOffset), toolFrame), toolFrame);
				toolFrame.getElement().getStyle().setZIndex(zIndex);
			}
		});
		return toolFrame;
	}

	protected void moveToolFrameBack(CanvasToolFrame toolFrame)
	{
		int zIndex = this.getElementZIndex(toolFrame.getElement());
		int newZIndex = Math.max(zIndex - 1, 1);
		if (zIndex == newZIndex)
		{
			return;
		}
		toolFrame.getElement().getStyle().setZIndex(newZIndex);
	}
	
	protected void moveToolFrameFront(CanvasToolFrame toolFrame)
	{
		int zIndex = this.getElementZIndex(toolFrame.getElement());
		int newZIndex = Math.min(zIndex + 1, ZIndexProvider.getLastAllocatedZIndex());
		if (zIndex == newZIndex)
		{
			return;
		}
		toolFrame.getElement().getStyle().setZIndex(newZIndex);
	}
	
	protected void setToolFramePosition(Point2D relativePos, final CanvasToolFrame toolFrame) {
		toolFrame.asWidget().getElement().getStyle().setLeft(relativePos.getX(), Unit.PX);
		toolFrame.asWidget().getElement().getStyle().setTop(relativePos.getY(), Unit.PX);
	}

	protected void startResizeCanvasToolFrame(final CanvasToolFrame toolFrame, final MouseDownEvent event)
	{
		final Point2D resizeStartPoint = new Point2D(event.getClientX(), event.getClientY());
		final Point2D resizeStartSize = new Point2D(toolFrame.getOffsetWidth(), toolFrame.getOffsetHeight());
		final RegistrationsManager resizeRegistrations = new RegistrationsManager();
		resizeRegistrations.add(this.worksheetPanel.addDomHandler(new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				onResizeMouseMove(toolFrame, resizeStartPoint, resizeStartSize, event);
			}}, MouseMoveEvent.getType()));
		resizeRegistrations.add(this.worksheetPanel.addDomHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				resizeRegistrations.clear();
			}}, MouseUpEvent.getType()));
	}
	
	protected void startDragCanvasToolFrame(final CanvasToolFrame toolFrame, final MouseEvent<?> startEvent) 
	{
		final Point2D toolFrameOffset = relativePosition(startEvent, toolFrame.getElement());
		final RegistrationsManager regs = new RegistrationsManager();
		NativeUtils.disableTextSelectInternal(this.worksheetPanel.getElement(), true);
		regs.add(this.worksheetPanel.addDomHandler(new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				onDragMouseMove(toolFrame, toolFrameOffset, event);
			}}, MouseMoveEvent.getType()));
		regs.add(this.worksheetPanel.addDomHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				regs.clear();
			}}, MouseUpEvent.getType()));
	}

	protected Point2D limitPosToWorksheet(Point2D pos, Widget elem) {
		Point2D result = new Point2D();
		int maxX = this.worksheetPanel.getOffsetWidth() - elem.getOffsetWidth();
		int maxY = this.worksheetPanel.getOffsetHeight() - elem.getOffsetHeight();
		result.setX(Math.max(0, Math.min(maxX, pos.getX())));
		result.setY(Math.max(0, Math.min(maxY, pos.getY())));
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
		activeToolRegistrations.clear();
	}
	
	protected void registerToolHandlers(ToolboxItem toolboxItem)
	{
		CanvasToolFactory<? extends CanvasTool<? extends ElementData>> toolFactory = 
			this.activeToolboxItem.getToolFactory();
		if (null == toolFactory)
		{
			return;
		}
		this.activeToolRegistrations.add(this.worksheetPanel.addDomHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				workSheetClicked(event);
			}}, ClickEvent.getType()));
		
		Widget floatingWidget = toolFactory.getFloatingWidget();
		if (null == floatingWidget)
		{
			return;
		}
		this.activeToolRegistrations.add(this.worksheetPanel.addDomHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				workSheetMouseOver(event);
			}}, MouseOverEvent.getType()));
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
		this.activeToolRegistrations.add(this.worksheetPanel.addDomHandler(new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				workSheetMouseMove(event);
			}}, MouseMoveEvent.getType()));
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
			toolData._position = new Point2D(x, y);
			toolData._ZIndex = this.getElementZIndex(toolInfo.toolFrame.getElement());
			toolData._size = new Point2D(
					toolInfo.toolFrame.getElement().getOffsetWidth(),
					toolInfo.toolFrame.getElement().getOffsetHeight());
			
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
		LinkedHashMap<Long, ElementData> updatedElements = new LinkedHashMap<Long, ElementData>();
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

	private void createToolInstancesFromData(LinkedHashMap<Long, ElementData> updatedElements) {
		for (ElementData newElement : updatedElements.values()) {
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
			//TODO: Refactor
			CanvasToolFrame toolFrame = this.createToolInstance(
					newElement._position, newElement._ZIndex, factory);
			if (null != newElement._size)
			{
				toolFrame.setWidth(newElement._size.getX());
				toolFrame.setHeight(newElement._size.getY());
			}
			toolFrame.getTool().setElementData(newElement);
			toolFrame.getTool().setFocus(false);
		}
	}
	
	protected int getElementZIndex(Element element)
	{
		try
		{
			return Integer.parseInt(element.getStyle().getZIndex());
		}
		catch(NumberFormatException ex)
		{
			return 0;
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

	private void onDragMouseMove(final CanvasToolFrame toolFrame,
			final Point2D toolFrameOffset, MouseMoveEvent event) {
		Point2D pos = relativePosition(event, worksheetPanel.getElement());
		pos.setX(pos.getX() - toolFrameOffset.getX());
		pos.setY(pos.getY() - toolFrameOffset.getY());
		setToolFramePosition(limitPosToWorksheet(pos, toolFrame), toolFrame);
	}
	
	private void onResizeMouseMove(final CanvasToolFrame toolFrame,
			final Point2D resizeStartPoint, final Point2D resizeStartSize, 
			MouseMoveEvent event) 
	{
		Point2D offsetPoint = new Point2D(event.getClientX(), event.getClientY());
		offsetPoint = offsetPoint.minus(resizeStartPoint);
		toolFrame.setWidth(resizeStartSize.getX() + offsetPoint.getX());
		toolFrame.setHeight(resizeStartSize.getY() + offsetPoint.getY());
	}
}
