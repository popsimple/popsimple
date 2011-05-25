package com.project.canvas.client.shared;

import java.util.ArrayList;

import com.google.gwt.event.shared.HandlerRegistration;

public class RegistrationsManager {
    protected final ArrayList<HandlerRegistration> _regs = new ArrayList<HandlerRegistration>();

    /**
     * Adds a HandlerRegistration to the manager.
     * @param reg can be null for convenience, will be safely ignored.
     */
    public void add(HandlerRegistration reg) {
        if (null != reg) {
            this._regs.add(reg);
        }
    }

    /**
     * Unregisters all the handlers added to the manager, and clears the manager.
     */
    public void clear() {
        for (HandlerRegistration reg : this._regs) {
            reg.removeHandler();
        }
        this._regs.clear();
    }
}
