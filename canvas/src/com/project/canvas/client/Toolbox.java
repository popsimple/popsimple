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
import com.project.canvas.client.canvastools.BuiltinTools;
import com.project.canvas.client.canvastools.CanvasToolFactory;
import com.project.canvas.client.resources.CanvasResources;
import com.project.canvas.client.shared.events.SimpleEvent;

public class Toolbox extends Composite {

	private static ToolboxUiBinder uiBinder = GWT.create(ToolboxUiBinder.class);

	interface ToolboxUiBinder extends UiBinder<Widget, Toolbox> {
	}

	@UiField
	FlowPanel toolsPanel;
	
	final ArrayList<CanvasToolFactory<?>> toolFactories = new ArrayList<CanvasToolFactory<?>>();
	final SimpleEvent<CanvasToolFactory<?>> toolChosenEvent = new SimpleEvent<CanvasToolFactory<?>>();

	final ArrayList<Widget> toolIconHolders = new ArrayList<Widget>();
	
	public Toolbox() {
		initWidget(uiBinder.createAndBindUi(this));
		
		for (CanvasToolFactory<?> factory : BuiltinTools.getTools()){
			this.addTool(factory);
		}
	}
	
	public SimpleEvent<CanvasToolFactory<?>> getToolChosenEvent() {
		return this.toolChosenEvent;
	}

	private void addTool(final CanvasToolFactory<?> factory) {
		this.toolFactories.add(factory);
		final FlowPanel outerElem = new FlowPanel();
		Label elem = new Label();
		outerElem.add(elem);
		this.toolsPanel.add(outerElem);
		this.toolIconHolders.add(outerElem);
		outerElem.addStyleName(CanvasResources.INSTANCE.main().toolboxCommonIconStyle());
		outerElem.setTitle(factory.getToolboxIconToolTip());
		
		elem.addStyleName(factory.getToolboxIconStyle());
		elem.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for (Widget w : toolIconHolders) {
					w.removeStyleName(CanvasResources.INSTANCE.main().toolboxCommonSelectedIconStyle());
				}
				outerElem.addStyleName(CanvasResources.INSTANCE.main().toolboxCommonSelectedIconStyle());
				toolChosenEvent.dispatch(factory);
			}
		});
	}

}
