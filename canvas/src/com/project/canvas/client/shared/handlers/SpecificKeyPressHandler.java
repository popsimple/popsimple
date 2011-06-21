package com.project.canvas.client.shared.handlers;

import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;

public abstract class SpecificKeyPressHandler implements KeyPressHandler {

    private int _keyCode;

    public SpecificKeyPressHandler(int keyCode)
    {
        this._keyCode = keyCode;
    }

    @Override
    public void onKeyPress(KeyPressEvent event) {
        if (event.getNativeEvent().getKeyCode() != this._keyCode)
        {
            return;
        }
        this.onSpecificKeyPress(event);
    }

    public abstract void onSpecificKeyPress(KeyPressEvent event);
}
