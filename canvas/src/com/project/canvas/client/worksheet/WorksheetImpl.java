package com.project.canvas.client.worksheet;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

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
import com.google.gwt.user.client.Event;
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
import com.project.canvas.client.shared.DialogWithZIndex;
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

public class WorksheetImpl extends Composite implements Worksheet {

	private static WorksheetImplUiBinder uiBinder = GWT.create(WorksheetImplUiBinder.class);

	interface WorksheetImplUiBinder extends UiBinder<Widget, WorksheetImpl> {
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
	
	@UiField
	HTMLPanel dragPanel;
	
	ToolboxItem activeToolboxItem;
	Widget activeToolFloatingWidget;
	
	protected final WorksheetOptionsWidget optionsWidget = new WorksheetOptionsWidget();
	protected final DialogBox optionsDialog = new DialogWithZIndex(false, true);

	public final SimpleEvent<Void> defaultToolRequestEvent = new SimpleEvent<Void>();
	public final SimpleEvent<Boolean> viewModeEvent = new SimpleEvent<Boolean>();
	
	/* (non-Javadoc)
	 * @see com.project.canvas.client.worksheet.Worksheet#getViewModeEvent()
	 */
	@Override
	public SimpleEvent<Boolean> getViewModeEvent() {
		return viewModeEvent;
	}

	/* (non-Javadoc)
	 * @see com.project.canvas.client.worksheet.Worksheet#getDefaultToolRequestEvent()
	 */
	@Override
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
		CanvasToolFrame toolFrame;
		HandlerRegistration killRegistration;
		@SuppressWarnings("unused")
		Date createdOn;
		@SuppressWarnings("unused")
		CanvasToolFactory<?> factory;
		RegistrationsManager registrations = new RegistrationsManager();
	}
	// Use LinkedHashMap in order to preserver the order of the tools.
	final LinkedHashMap<CanvasTool<? extends ElementData>, ToolInstanceInfo> toolRegsMap = new LinkedHashMap<CanvasTool<? extends ElementData>, ToolInstanceInfo>();

	protected CanvasPage page = new CanvasPage();

	private CanvasTool<? extends ElementData> activeToolInstance;


	public WorksheetImpl() {
		initWidget(uiBinder.createAndBindUi(this));
		optionsDialog.setText("Worksheet options");
		optionsDialog.add(this.optionsWidget);
		setRegistrations();
		this.dragPanel.setVisible(false);
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
		final WorksheetImpl that = this;
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
		return new Point2D(event.getRelativeX(elem), event.getRelativeY(elem));
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
		ToolInstanceInfo toolInfo = new ToolInstanceInfo(toolFactory, toolFrame, null);
		this.toolRegsMap.put(tool, toolInfo);
		
		//TODO: Remove registrations when tool is killed?
		RegistrationsManager regs = toolInfo.registrations;
		regs.add(toolFrame.getCloseRequest().addHandler(new SimpleEvent.Handler<Void>() {
			@Override
			public void onFire(Void arg) {
				removeToolInstance(toolFrame);
			}
		}));
		regs.add(toolFrame.getMoveStartRequest().addHandler(new SimpleEvent.Handler<MouseDownEvent>() {
			@Override
			public void onFire(MouseDownEvent arg) {
				startDragCanvasToolFrame(toolFrame, arg);
			}
		}));
		regs.add(toolFrame.addResizeStartRequestHandler(new SimpleEvent.Handler<MouseDownEvent>() {
			@Override
			public void onFire(MouseDownEvent arg) {
				startResizeCanvasToolFrame(toolFrame, arg);
			}
		}));
		regs.add(toolFrame.addMoveBackRequestHandler(new SimpleEvent.Handler<Void>() {
			@Override
			public void onFire(Void arg) {
				moveToolFrameBack(toolFrame);
			}}));
		
		regs.add(toolFrame.addMoveFrontRequestHandler(new SimpleEvent.Handler<Void>() {
			@Override
			public void onFire(Void arg) {
				moveToolFrameFront(toolFrame);
			}}));
		
		this.worksheetPanel.add(toolFrame);
		toolInfo.killRegistration = tool.getKillRequestedEvent().addHandler(new SimpleEvent.Handler<String>() {
			public void onFire(String arg) {
				removeToolInstance(toolFrame);
			}
		});
		regs.add(toolInfo.killRegistration);
		tool.asWidget().setVisible(false);
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				tool.asWidget().setVisible(true);
				setActiveTool(tool);
				setToolFramePosition(limitPosToWorksheet(relativePos.plus(creationOffset), toolFrame), toolFrame);
				toolFrame.getElement().getStyle().setZIndex(zIndex);
			}
		});
		return toolFrame;
	}
	
	//TODO: Move
	public class Rectangle
	{
		protected int _left = 0;
		protected int _top = 0;
		protected int _right = 0;
		protected int _bottom = 0;
		
		public Rectangle(int left, int top, int right, int bottom)
		{
			this._left = left;
			this._top = top;
			this._right = right;
			this._bottom = bottom;
		}
		
		public boolean isOverlapping(Rectangle rect)
		{
			if (this._right < rect._left)
			{
				return false;
			}
			if (this._left > rect._right)
			{
				return false;
			}
			if (this._bottom < rect._top)
			{
				return false;
			}
			if (this._top > rect._bottom)
			{
				return false;
			}
			return true;
		}
	}
	
	protected Rectangle getElementRectangle(Element element)
	{
		return new Rectangle(element.getAbsoluteLeft(), element.getAbsoluteTop(),
				element.getAbsoluteRight(), element.getAbsoluteBottom());
	}
	
	protected TreeMap<Integer, CanvasToolFrame> getTopOverlappingFrames(CanvasToolFrame toolFrame)
	{
		TreeMap<Integer, CanvasToolFrame> sortedMap = new TreeMap<Integer, CanvasToolFrame>();
		int currentZIndex = this.getElementZIndex(toolFrame.getElement());
		for (Entry<CanvasTool<? extends ElementData>, ToolInstanceInfo>  entry : toolRegsMap.entrySet())
		{
			if (entry.getValue().toolFrame == toolFrame)
			{
				continue;
			}
			int zIndex = this.getElementZIndex(entry.getValue().toolFrame.getElement());
			if (zIndex < currentZIndex)
			{
				continue;
			}
			if (false == this.isOverlappingElements(
					toolFrame.getElement(), entry.getValue().toolFrame.getElement()))
			{
				continue;
			}
			sortedMap.put(zIndex, entry.getValue().toolFrame);
		}
		return sortedMap;
	}
	
	protected TreeMap<Integer, CanvasToolFrame> getBottomOverlappingFrames(CanvasToolFrame toolFrame)
	{
		TreeMap<Integer, CanvasToolFrame> sortedMap = new TreeMap<Integer, CanvasToolFrame>();
		int currentZIndex = this.getElementZIndex(toolFrame.getElement());
		for (Entry<CanvasTool<? extends ElementData>, ToolInstanceInfo>  entry : toolRegsMap.entrySet())
		{
			if (entry.getValue().toolFrame == toolFrame)
			{
				continue;
			}
			int zIndex = this.getElementZIndex(entry.getValue().toolFrame.getElement());
			if (zIndex > currentZIndex)
			{
				continue;
			}
			if (false == this.isOverlappingElements(
					toolFrame.getElement(), entry.getValue().toolFrame.getElement()))
			{
				continue;
			}
			sortedMap.put(zIndex, entry.getValue().toolFrame);
		}
		return sortedMap;
	}
	
	protected boolean isOverlappingElements(Element element1, Element element2)
	{
		return this.getElementRectangle(element1).isOverlapping(
				this.getElementRectangle(element2));
		
	}
	
	protected void moveToolFrameBack(CanvasToolFrame toolFrame)
	{
		TreeMap<Integer, CanvasToolFrame> overlappingFrames = this.getBottomOverlappingFrames(toolFrame);
		if (overlappingFrames.isEmpty())
		{
			return;
		}
		if (overlappingFrames.lastKey() > 1)
		{
			toolFrame.getElement().getStyle().setZIndex(overlappingFrames.lastKey() + -1);
		}
		else
		{
			toolFrame.getElement().getStyle().setZIndex(1);
		}
	}
	
	protected void moveToolFrameFront(CanvasToolFrame toolFrame)
	{
		TreeMap<Integer, CanvasToolFrame> overlappingFrames = this.getTopOverlappingFrames(toolFrame);
		if (overlappingFrames.isEmpty())
		{
			return;
		}
		if (overlappingFrames.firstKey() < ZIndexProvider.getLastAllocatedZIndex())
		{
			toolFrame.getElement().getStyle().setZIndex(overlappingFrames.firstKey() + 1);
		}
		else
		{
			toolFrame.getElement().getStyle().setZIndex(ZIndexProvider.allocateZIndex());
		}
	}
	
	protected void setToolFramePosition(Point2D relativePos, final CanvasToolFrame toolFrame) {
		toolFrame.asWidget().getElement().getStyle().setLeft(relativePos.getX(), Unit.PX);
		toolFrame.asWidget().getElement().getStyle().setTop(relativePos.getY(), Unit.PX);
	}
	protected void startDragCanvasToolFrame(final CanvasToolFrame toolFrame, final MouseEvent<?> startEvent)
	{
		final SimpleEvent.Handler<Point2D> dragHandler = new SimpleEvent.Handler<Point2D>() {
			@Override
			public void onFire(Point2D pos) {
				setToolFramePosition(limitPosToWorksheet(pos, toolFrame), toolFrame);
			}
		};
		SimpleEvent.Handler<Void> stopMoveHandler = new SimpleEvent.Handler<Void>(){
			@Override
			public void onFire(Void arg) {
				toolFrame.removeStyleName(CanvasResources.INSTANCE.main().hover());
				toolFrame.removeStyleName(CanvasResources.INSTANCE.main().drag());
			}};
		toolFrame.addStyleName(CanvasResources.INSTANCE.main().hover());
		toolFrame.addStyleName(CanvasResources.INSTANCE.main().drag());
		this.startMouseMoveOperation(this.dragPanel.getElement(), relativePosition(startEvent, toolFrame.getElement()), 
				dragHandler, stopMoveHandler);
	}

	protected void startResizeCanvasToolFrame(final CanvasToolFrame toolFrame, final MouseEvent<?>  startEvent)
	{
		final SimpleEvent.Handler<Point2D> resizeHandler = new SimpleEvent.Handler<Point2D>() {
			@Override
			public void onFire(Point2D size) {
				resizeToolFrame(toolFrame, size);
			}
		};
		this.startMouseMoveOperation(toolFrame.getElement(), Point2D.zero, resizeHandler, null);
	}

	
	protected void startMouseMoveOperation(final Element referenceElem, final Point2D referenceOffset, 
			final SimpleEvent.Handler<Point2D> moveHandler,
			final SimpleEvent.Handler<Void> stopMoveHandler) 
	{
		final RegistrationsManager regs = new RegistrationsManager();
		
		NativeUtils.disableTextSelectInternal(this.worksheetPanel.getElement(), true);
		regs.add(this.dragPanel.addDomHandler(new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				Point2D pos = relativePosition(event, referenceElem);
				moveHandler.onFire(pos.minus(referenceOffset));
				event.stopPropagation();
			}}, MouseMoveEvent.getType()));
		regs.add(this.dragPanel.addDomHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				NativeUtils.disableTextSelectInternal(worksheetPanel.getElement(), false);
				Event.releaseCapture(dragPanel.getElement());
				regs.clear();
				dragPanel.setVisible(false);
				if (null != stopMoveHandler) {
					stopMoveHandler.onFire(null);
				}
			}}, MouseUpEvent.getType()));

		
		Event.setCapture(this.dragPanel.getElement());
		this.dragPanel.setVisible(true);
	}

	protected Point2D limitPosToWorksheet(Point2D pos, Widget elem) {
		Point2D maxSize = new Point2D(
				this.worksheetPanel.getOffsetWidth() - elem.getOffsetWidth(),
				this.worksheetPanel.getOffsetHeight() - elem.getOffsetHeight());
		
		return Point2D.max(Point2D.zero, Point2D.min(maxSize, pos));
	}

	protected void removeToolInstance(CanvasToolFrame toolFrame) {
		ToolInstanceInfo info = this.toolRegsMap.remove(toolFrame.getTool());
		this.worksheetPanel.remove(toolFrame);
		info.registrations.clear();
	}

	/* (non-Javadoc)
	 * @see com.project.canvas.client.worksheet.Worksheet#setActiveTool(com.project.canvas.client.canvastools.base.ToolboxItem)
	 */
	@Override
	public void setActiveTool(ToolboxItem toolboxItem) {
		this.clearActiveToolboxItem();
		
		this.activeToolboxItem = toolboxItem;
		this.worksheetPanel.addStyleName(toolboxItem.getCanvasStyleInCreateMode());
		
		this.registerToolHandlers(this.activeToolboxItem);
	}
	
	protected void clearActiveToolboxItem()
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
	
	public void save() {
		ArrayList<ElementData> activeElems = new ArrayList<ElementData>();
		for (Entry<CanvasTool<? extends ElementData>, ToolInstanceInfo>  entry : toolRegsMap.entrySet())
		{
			CanvasTool<? extends ElementData> tool = entry.getKey();
			ToolInstanceInfo toolInfo = entry.getValue();
			ElementData toolData = tool.getValue();
			int x = Integer.valueOf(toolInfo.toolFrame.getElement().getOffsetLeft());
			int y = Integer.valueOf(toolInfo.toolFrame.getElement().getOffsetTop());
			toolData._position = new Point2D(x, y);
			toolData._zIndex = this.getElementZIndex(toolInfo.toolFrame.getElement());
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
			CanvasToolFrame toolFrame = this.createToolInstance(newElement._position, newElement._zIndex, factory);
			if (null != newElement._size)
			{
				resizeToolFrame(toolFrame, newElement._size);
			}
			toolFrame.getTool().setElementData(newElement);
			toolFrame.getTool().setActive(false);
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

	/* (non-Javadoc)
	 * @see com.project.canvas.client.worksheet.Worksheet#load(java.lang.String)
	 */
	@Override
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

	public void resizeToolFrame(final CanvasToolFrame toolFrame, Point2D pos) {
		toolFrame.setWidth(pos.getX());
		toolFrame.setHeight(pos.getY());
	}

	public void setActiveTool(final CanvasTool<? extends ElementData> tool) {
		if (null != this.activeToolInstance) {
			this.activeToolInstance.setActive(false);
		}
		this.activeToolInstance = tool;
		tool.setActive(true);
	}
}