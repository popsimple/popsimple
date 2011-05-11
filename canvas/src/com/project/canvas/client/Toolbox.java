package com.project.canvas.client;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.project.canvas.client.canvastools.base.BuiltinTools;
import com.project.canvas.client.canvastools.base.ToolboxItem;
import com.project.canvas.client.resources.CanvasResources;
import com.project.canvas.client.shared.events.SimpleEvent;

public class Toolbox extends Composite {

	private static ToolboxUiBinder uiBinder = GWT.create(ToolboxUiBinder.class);

	interface ToolboxUiBinder extends UiBinder<Widget, Toolbox> {
	}

	@UiField
	FlowPanel toolsPanel;
	
	final ArrayList<ToolboxItem> toolboxItems = new ArrayList<ToolboxItem>();
	final SimpleEvent<ToolboxItem> toolChosenEvent = new SimpleEvent<ToolboxItem>();

	final ArrayList<Widget> toolIconHolders = new ArrayList<Widget>();
	
	public Toolbox() {
		initWidget(uiBinder.createAndBindUi(this));
		
		for (ToolboxItem toolboxItem : BuiltinTools.getTools()){
			this.addTool(toolboxItem);
		}
	}
	
	public SimpleEvent<ToolboxItem> getToolChosenEvent() {
		return this.toolChosenEvent;
	}
	
	private void addTool(final ToolboxItem toolboxItem) {
		this.toolboxItems.add(toolboxItem);
		final FlowPanel outerElem = new FlowPanel();
		Label elem = new Label();
		outerElem.add(elem);
		this.toolsPanel.add(outerElem);
		this.toolIconHolders.add(outerElem);
		outerElem.addStyleName(CanvasResources.INSTANCE.main().toolboxCommonIconStyle());
		outerElem.setTitle(toolboxItem.getToolboxIconToolTip());
		
		elem.addStyleName(toolboxItem.getToolboxIconStyle());
		elem.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				for (Widget w : toolIconHolders) {
					w.removeStyleName(CanvasResources.INSTANCE.main().toolboxCommonSelectedIconStyle());
				}
				outerElem.addStyleName(CanvasResources.INSTANCE.main().toolboxCommonSelectedIconStyle());
				toolChosenEvent.dispatch(toolboxItem);
			}
		});
	}

}
