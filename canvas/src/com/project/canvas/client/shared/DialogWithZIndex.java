package com.project.canvas.client.shared;

import com.google.gwt.user.client.ui.DialogBox;

public class DialogWithZIndex extends DialogBox {
	public DialogWithZIndex(boolean autoHide, boolean modal) {
		super(autoHide, modal);
		super.setGlassEnabled(true);
	}

	@Override
	public void show() {
		ZIndexAllocator.allocateSetZIndex(super.getGlassElement());
		ZIndexAllocator.allocateSetZIndex(super.getElement());
		super.show();
	}
}
