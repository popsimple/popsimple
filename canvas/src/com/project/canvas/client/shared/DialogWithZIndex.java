package com.project.canvas.client.shared;

import com.google.gwt.user.client.ui.DialogBox;

public class DialogWithZIndex extends DialogBox 
{
	public DialogWithZIndex(boolean autoHide, boolean modal) {
		super(autoHide,modal);
		super.setGlassEnabled(true);
	}

	@Override
	public void show() {
		super.getGlassElement().getStyle().setZIndex(ZIndexProvider.allocateZIndex());
		super.getElement().getStyle().setZIndex(ZIndexProvider.allocateZIndex());
		super.show();
	}
}
