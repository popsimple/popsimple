package com.project.canvas.client;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.project.canvas.client.canvastools.CanvasTool;
import com.project.canvas.client.canvastools.CanvasToolFactory;
import com.project.canvas.client.events.SimpleEvent;

public class Worksheet extends Composite {

	private static WorksheetUiBinder uiBinder = GWT.create(WorksheetUiBinder.class);

	interface WorksheetUiBinder extends UiBinder<Widget, Worksheet> {
	}

	@UiField
	FlowPanel worksheetPanel;
	
	CanvasToolFactory<?> activeToolFactory;

	private class ToolInstanceInfo {
		public ToolInstanceInfo(CanvasToolFactory<?> factory, HandlerRegistration killRegistration) {
			super();
			this.factory = factory;
			this.killRegistration = killRegistration;
		}
		CanvasToolFactory<?> factory;
		HandlerRegistration killRegistration; 
	}
	final HashMap<CanvasTool, ToolInstanceInfo> toolRegsMap = new HashMap<CanvasTool, ToolInstanceInfo>();
	
	public Worksheet() {
		initWidget(uiBinder.createAndBindUi(this));
		this.worksheetPanel.addDomHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				workSheetClicked(event);
			}}, ClickEvent.getType());
	}

	protected void workSheetClicked(ClickEvent event) {
		if (null == this.activeToolFactory) {
			return;
		}
		createToolInstance(event);
	}

	private void createToolInstance(ClickEvent event) {
		final CanvasTool tool = this.activeToolFactory.create();
		tool.asWidget().getElement().getStyle().setLeft(event.getRelativeX(this.worksheetPanel.getElement()), Unit.PX);
		tool.asWidget().getElement().getStyle().setTop(event.getRelativeY(this.worksheetPanel.getElement()), Unit.PX);
		this.worksheetPanel.add(tool);
		HandlerRegistration reg = tool.getKillRequestedEvent().addHandler(new SimpleEvent.Handler<String>() {
			@Override
			public void onFire(String arg) {
				removeToolInstance(tool);
			}
		});
		this.toolRegsMap.put(tool, new ToolInstanceInfo(this.activeToolFactory, reg));
		tool.setFocus(true);
	}

	protected void removeToolInstance(CanvasTool tool) {
		ToolInstanceInfo info = this.toolRegsMap.remove(tool);
		this.worksheetPanel.remove(tool);
		this.worksheetPanel.removeStyleName(info.factory.getCanvasStyleInCreateMode());
		info.killRegistration.removeHandler();
	}

	public void setActiveTool(CanvasToolFactory<?> factory) {
		this.activeToolFactory = factory;
		this.worksheetPanel.addStyleName(factory.getCanvasStyleInCreateMode());
	}
}
