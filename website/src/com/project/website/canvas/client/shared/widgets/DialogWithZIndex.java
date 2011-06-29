package com.project.website.canvas.client.shared.widgets;

import com.google.gwt.user.client.ui.DialogBox;
import com.project.website.canvas.client.shared.ZIndexAllocator;

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
