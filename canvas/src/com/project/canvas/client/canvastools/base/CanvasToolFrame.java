package com.project.canvas.client.canvastools.base;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class CanvasToolFrame extends Composite {

	private static CanvasToolFrameUiBinder uiBinder = GWT
			.create(CanvasToolFrameUiBinder.class);

	interface CanvasToolFrameUiBinder extends UiBinder<Widget, CanvasToolFrame> {
	}

	@UiField
	HTMLPanel toolPanel;
	
	protected final CanvasTool<?> tool;
	
	public CanvasToolFrame(CanvasTool<?> canvasTool) {
		initWidget(uiBinder.createAndBindUi(this));
		CanvasToolCommon.stopClickPropagation(this);
		this.tool = canvasTool;
		this.toolPanel.add(canvasTool);
	}
	
	public CanvasTool<?> getTool() {
		return this.tool;
	}
}
