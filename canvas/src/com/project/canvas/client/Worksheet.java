package com.project.canvas.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.touch.client.Point;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.project.canvas.client.canvastools.base.CanvasTool;
import com.project.canvas.client.canvastools.base.CanvasToolFactory;
import com.project.canvas.client.canvastools.base.CanvasToolFrame;
import com.project.canvas.client.canvastools.base.ToolboxItem;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.shared.contracts.CanvasService;
import com.project.canvas.shared.contracts.CanvasServiceAsync;
import com.project.canvas.shared.data.CanvasPage;
import com.project.canvas.shared.data.ElementData;

public class Worksheet extends Composite {

	private static WorksheetUiBinder uiBinder = GWT.create(WorksheetUiBinder.class);

	interface WorksheetUiBinder extends UiBinder<Widget, Worksheet> {
	}

	@UiField
	FlowPanel worksheetPanel;

	@UiField
	Button saveButton;
	
	ToolboxItem activeToolboxItem;

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
	final HashMap<CanvasTool<ElementData>, ToolInstanceInfo> toolRegsMap = new HashMap<CanvasTool<ElementData>, ToolInstanceInfo>();

	protected CanvasPage page = new CanvasPage();
	
	public Worksheet() {
		initWidget(uiBinder.createAndBindUi(this));
		this.worksheetPanel.addDomHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				workSheetClicked(event);
			}}, ClickEvent.getType());
		this.saveButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				save();
			}
		});
	}

	protected void workSheetClicked(ClickEvent event) {
		if (null == this.activeToolboxItem) {
			return;
		}
		CanvasToolFactory<?> toolFactory = this.activeToolboxItem.getToolFactory();
		if (null == toolFactory)
		{
			return;
		}
		createToolInstance(event, toolFactory);
	}

	private void createToolInstance(ClickEvent event, CanvasToolFactory<?> toolFactory) {
		CanvasTool tool = toolFactory.create();
		final CanvasToolFrame toolFrame = new CanvasToolFrame(tool);
		
		toolFrame.asWidget().getElement().getStyle().setLeft(event.getRelativeX(this.worksheetPanel.getElement()), Unit.PX);
		toolFrame.asWidget().getElement().getStyle().setTop(event.getRelativeY(this.worksheetPanel.getElement()), Unit.PX);
		
		this.worksheetPanel.add(toolFrame);
		HandlerRegistration reg = tool.getKillRequestedEvent().addHandler(new SimpleEvent.Handler<String>() {
			public void onFire(String arg) {
				removeToolInstance(toolFrame);
			}
		});
		this.toolRegsMap.put(tool, new ToolInstanceInfo(toolFactory, toolFrame, reg));
		tool.setFocus(true);
	}

	protected void removeToolInstance(CanvasToolFrame toolFrame) {
		ToolInstanceInfo info = this.toolRegsMap.remove(toolFrame.getTool());
		this.worksheetPanel.remove(toolFrame);
		info.killRegistration.removeHandler();
	}

	public void setActiveTool(ToolboxItem toolboxItem) {
		if (null != this.activeToolboxItem)
		{
			this.worksheetPanel.removeStyleName(this.activeToolboxItem.getCanvasStyleInCreateMode());
		}
		this.activeToolboxItem = toolboxItem;
		this.worksheetPanel.addStyleName(toolboxItem.getCanvasStyleInCreateMode());
	}

	protected void save() {
		ArrayList<ElementData> activeElems = new ArrayList<ElementData>();
		for (Entry<CanvasTool<ElementData>, ToolInstanceInfo>  entry : toolRegsMap.entrySet())
		{
			CanvasTool<ElementData> tool = entry.getKey();
			ToolInstanceInfo toolInfo = entry.getValue();
			int x = Integer.valueOf(toolInfo.toolFrame.getElement().getOffsetLeft());
			int y = Integer.valueOf(toolInfo.toolFrame.getElement().getOffsetTop());
			ElementData toolData = tool.getData();
			toolData.position = new Point(x,y);
			activeElems.add(toolData);
		}
		this.page.elements.clear();
		this.page.elements.addAll(activeElems);
		
		CanvasServiceAsync service = (CanvasServiceAsync)GWT.create(CanvasService.class);
		Window.setStatus("Saving page...");
		this.saveButton.setEnabled(false);
		service.SavePage(page, new AsyncCallback<CanvasPage>() {
			@Override
			public void onSuccess(CanvasPage result) {
				saveButton.setEnabled(true);
				Window.setStatus(null);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Save failed. Reason: " + caught.toString());
				saveButton.setEnabled(true);
				Window.setStatus(null);
			}
		});
	}
}