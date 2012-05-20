package com.project.shared.client.utils.widgets;

import com.google.gwt.user.client.ui.DialogBox;
import com.project.shared.client.utils.ZIndexAllocator;

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
