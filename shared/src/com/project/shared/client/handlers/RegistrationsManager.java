package com.project.shared.client.handlers;

import java.util.Collection;

import com.google.gwt.event.shared.HandlerRegistration;
import com.project.shared.data.funcs.Func;

public class RegistrationsManager {
    protected final KeyedRegistrationsManager _keyedRegs;
    protected final Object _key;
    
    public RegistrationsManager(KeyedRegistrationsManager keyedRegs, Object key)
    {
        this._keyedRegs = keyedRegs;
        this._key = key;
    }
    
    public RegistrationsManager()
    {
        this(new KeyedRegistrationsManager(), new Object());
    }
    
    /**
     * Adds a HandlerRegistration to the manager.
     * @param reg can be null for convenience, will be safely ignored.
     */
    public void add(HandlerRegistration reg) {
        this._keyedRegs.add(this._key, reg);
    }

    /**
     * Adds a HandlerRegistrations to the manager.
     * @param reg can be null for convenience, will be safely ignored.
     */
    public void addAll(Collection<HandlerRegistration> regs) {
        this._keyedRegs.addAll(this._key, regs);
    }

    /**
     * Unregisters all the handlers added to the manager, and clears the manager.
     */
    public void clear() {
        this._keyedRegs.clear(this._key);
    }
    
    public void addRecurringMultiple(Func<Void, Iterable<HandlerRegistration>> registerFunc)
    {
        this._keyedRegs.addRecurringMultiple(this._key, registerFunc);
    }
    
    /***
     * Convenience wrapper for single registration calls to addRecurring(Func<Void, Iterable<HandlerRegistration>> registerFunc)
     * @param registerFunc
     */
    public void addRecurring(Func<Void, HandlerRegistration> registerFunc)
    {
        this._keyedRegs.addRecurring(this._key, registerFunc);
    }

    public boolean hasRegistrations()
    {
        return this._keyedRegs.hasRegistrations(this._key);
    }

    public HandlerRegistration asSingleRegistration()
    {
        final RegistrationsManager that = this;
        return new HandlerRegistration() {
            @Override
            public void removeHandler()
            {
                that.clear();
            }
        };
    }
}
