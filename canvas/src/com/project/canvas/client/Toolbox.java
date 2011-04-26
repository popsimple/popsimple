package com.project.canvas.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class Toolbox extends Composite {

	private static ToolboxUiBinder uiBinder = GWT.create(ToolboxUiBinder.class);

	interface ToolboxUiBinder extends UiBinder<Widget, Toolbox> {
	}

	public Toolbox() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
