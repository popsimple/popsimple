package com.project.canvas.client.shared;

import java.util.ArrayList;
import java.util.Collection;

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
     * Adds a HandlerRegistrations to the manager.
     * @param reg can be null for convenience, will be safely ignored.
     */
    public void addAll(Collection<HandlerRegistration> regs) {
        this._regs.addAll(regs);
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

    public ArrayList<HandlerRegistration> getRegistrations()
    {
        return this._regs;
    }
}
