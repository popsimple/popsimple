package com.project.canvas.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class CanvasContainer extends Composite {

	private static CanvasContainerUiBinder uiBinder = GWT
			.create(CanvasContainerUiBinder.class);

	interface CanvasContainerUiBinder extends UiBinder<Widget, CanvasContainer> {
	}

	public CanvasContainer() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
