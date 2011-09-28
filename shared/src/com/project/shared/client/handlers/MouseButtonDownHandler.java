package com.project.shared.client.handlers;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.project.shared.client.utils.MouseButtonUtils;
import com.project.shared.data.MouseButtons;

public abstract class MouseButtonDownHandler implements MouseDownHandler {

    private int _nativeButton;

    public MouseButtonDownHandler(MouseButtons mouseButton)
    {
        this._nativeButton = MouseButtonUtils.toNativeButton(mouseButton);
    }

    @Override
    public void onMouseDown(MouseDownEvent event) {
        if (event.getNativeButton() != this._nativeButton)
        {
            return;
        }
        this.onMouseButtonDown(event);
    }

    public abstract void onMouseButtonDown(MouseDownEvent event);
}
