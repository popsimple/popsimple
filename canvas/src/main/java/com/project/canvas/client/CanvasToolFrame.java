package com.project.canvas.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.project.canvas.client.canvastools.CanvasTool;

public class CanvasToolFrame extends Composite {

	private static CanvasToolFrameUiBinder uiBinder = GWT
			.create(CanvasToolFrameUiBinder.class);

	interface CanvasToolFrameUiBinder extends UiBinder<Widget, CanvasToolFrame> {
	}

	@UiField
	HTMLPanel toolPanel;
	
	public CanvasToolFrame(CanvasTool canvasTool) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.toolPanel.add(canvasTool);
	}
}
