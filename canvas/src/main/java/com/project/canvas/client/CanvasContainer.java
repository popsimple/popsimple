package com.project.canvas.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.project.canvas.client.canvastools.CanvasToolFactory;
import com.project.canvas.client.events.SimpleEvent;

public class CanvasContainer extends Composite {

	private static CanvasContainerUiBinder uiBinder = GWT
			.create(CanvasContainerUiBinder.class);

	interface CanvasContainerUiBinder extends UiBinder<Widget, CanvasContainer> {
	}

	
	@UiField
	Toolbox toolbox;
	@UiField
	Worksheet worksheet;
	
	public CanvasContainer() {
		initWidget(uiBinder.createAndBindUi(this));
		this.toolbox.getToolChosenEvent().addHandler(new SimpleEvent.Handler<CanvasToolFactory<?>>() {
			public void onFire(CanvasToolFactory<?> arg) {
				worksheet.setActiveTool(arg);
			}
		});
	}

}
