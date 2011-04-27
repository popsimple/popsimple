package com.project.canvas.client;

import java.util.Date;
import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.project.canvas.client.canvastools.CanvasTool;
import com.project.canvas.client.canvastools.CanvasToolFactory;
import com.project.canvas.client.canvastools.ToolboxItem;
import com.project.canvas.client.shared.events.SimpleEvent;

public class Worksheet extends Composite {

	private static WorksheetUiBinder uiBinder = GWT.create(WorksheetUiBinder.class);

	interface WorksheetUiBinder extends UiBinder<Widget, Worksheet> {
	}

	@UiField
	FlowPanel worksheetPanel;
	
	ToolboxItem activeToolboxItem;

	private class ToolInstanceInfo {
		public ToolInstanceInfo(CanvasToolFactory<?> factory, HandlerRegistration killRegistration) {
			super();
			this.factory = factory;
			this.killRegistration = killRegistration;
			this.createdOn = new Date();
		}
		CanvasToolFactory<?> factory;
		HandlerRegistration killRegistration;
		Date createdOn;
	}
	final HashMap<CanvasTool, ToolInstanceInfo> toolRegsMap = new HashMap<CanvasTool, ToolInstanceInfo>();
	
	public Worksheet() {
		initWidget(uiBinder.createAndBindUi(this));
		this.worksheetPanel.addDomHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				workSheetClicked(event);
			}}, ClickEvent.getType());
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
		final CanvasTool tool = toolFactory.create();
		tool.asWidget().getElement().getStyle().setLeft(event.getRelativeX(this.worksheetPanel.getElement()), Unit.PX);
		tool.asWidget().getElement().getStyle().setTop(event.getRelativeY(this.worksheetPanel.getElement()), Unit.PX);
		this.worksheetPanel.add(tool);
		HandlerRegistration reg = tool.getKillRequestedEvent().addHandler(new SimpleEvent.Handler<String>() {
			public void onFire(String arg) {
				removeToolInstance(tool);
			}
		});
		this.toolRegsMap.put(tool, new ToolInstanceInfo(toolFactory, reg));
		tool.setFocus(true);
	}

	protected void removeToolInstance(CanvasTool tool) {
		ToolInstanceInfo info = this.toolRegsMap.remove(tool);
		this.worksheetPanel.remove(tool);
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
}
