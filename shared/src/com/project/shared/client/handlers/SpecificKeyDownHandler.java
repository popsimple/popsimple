package com.project.shared.client.handlers;

import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;

public abstract class SpecificKeyDownHandler implements KeyDownHandler {

        private int _keyCode;

        @Override
        public void onKeyDown(KeyDownEvent event) {
            if (this._keyCode != event.getNativeKeyCode())
            {
                return;
            }
            this.onSpecificKeyDown(event);
        }

        public SpecificKeyDownHandler(int keyCode)
        {
            this._keyCode = keyCode;
        }

        public abstract void onSpecificKeyDown(KeyDownEvent event);
}
