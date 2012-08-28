package com.project.shared.client.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gwt.event.shared.HandlerRegistration;
import com.project.shared.client.utils.ListUtils;
import com.project.shared.data.funcs.Func;
import com.project.shared.utils.MapUtils;

public class KeyedRegistrationsManager {
    private final HashMap<Object, ArrayList<HandlerRegistration>> _regs = new HashMap<Object, ArrayList<HandlerRegistration>>();
    private final HashMap<Object, ArrayList<Func<Void, Iterable<HandlerRegistration>>>> _scheduledRegs = new HashMap<Object, ArrayList<Func<Void,Iterable<HandlerRegistration>>>>();
    
    private boolean isEnabled;
    
    public KeyedRegistrationsManager()
    {
        this(true);
    }
    
    public KeyedRegistrationsManager(boolean isEnabled)
    {
        this.setEnabled(isEnabled);
    }
    
    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
        if (isEnabled) {
            for (Entry<Object, ArrayList<Func<Void, Iterable<HandlerRegistration>>>> entry : this._scheduledRegs.entrySet())
            {
                for (Func<Void, Iterable<HandlerRegistration>> func : entry.getValue()) 
                {
                    this.addAll(entry.getKey(), func.apply(null));
                }
            }
        }
        else {
            this.clearAll();
        }
    }

    public void addRecurring(Object key, final Func<Void, HandlerRegistration> registerFunc)
    {
        this.getScheduledRegs(key).add(new Func<Void, Iterable<HandlerRegistration>>(){
            @Override public Iterable<HandlerRegistration> apply(Void arg) {
                return ListUtils.create(registerFunc.apply(null));
            }});
        if (this.isEnabled) {
            this.add(key, registerFunc.apply(null));
        }
    }

    public void addRecurringMultiple(Object key, Func<Void, Iterable<HandlerRegistration>> registerFunc) 
    {
        this.getScheduledRegs(key).add(registerFunc);
        if (this.isEnabled) {
            this.addAll(key, registerFunc.apply(null));
        }
    }
    
    /**
     * Adds a HandlerRegistration to the manager.
     * @param reg can be null for convenience, will be safely ignored.
     */
    public void add(Object key, HandlerRegistration reg) {
        if (null != reg) {
            this.getHandlersList(key).add(reg);
        }
    }


    public RegistrationsManager asRegistrationsManager(Object key)
    {
        return new RegistrationsManager(this, key);
    }
    
    /**
     * Adds a HandlerRegistrations to the manager.
     * @param reg can be null for convenience, will be safely ignored.
     */
    public void addAll(Object key, Iterable<HandlerRegistration> regs) {
        ListUtils.addAll(this.getHandlersList(key), regs);
    }

    public void clear(Object key)
    {
        if (false == this._regs.containsKey(key)) 
        {
            return; // no errors. we don't care about trying to clear when there's nothing registered on this key.
        }
        ArrayList<HandlerRegistration> regs = this._regs.get(key);
        for (HandlerRegistration reg : regs)
        {
            reg.removeHandler();
        }
        regs.clear();
        this._regs.remove(key);
    }
    
    /**
     * Unregisters all the handlers added to the manager, and clears the manager.
     */
    public void clearAll() {
        for (ArrayList<HandlerRegistration> regs : this._regs.values()) {
            for (HandlerRegistration reg : regs) {
                reg.removeHandler();
            }
            regs.clear();
        }
        this._regs.clear();
    }

    public boolean hasRegistrations(Object key)
    {
        if (false == this._regs.containsKey(key))
        {
            return false;
        }
        return false == this._regs.get(key).isEmpty();
    }
    
    public boolean hasRegistrations()
    {
        for (ArrayList<HandlerRegistration> regs : this._regs.values()) {
            if (false == regs.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public HandlerRegistration asSingleRegistration()
    {
        final KeyedRegistrationsManager that = this;
        return new HandlerRegistration() {
            @Override
            public void removeHandler()
            {
                that.clearAll();
            }
        };
    }
    
    private ArrayList<Func<Void, Iterable<HandlerRegistration>>> getScheduledRegs(Object key) 
    {
        return MapUtils.getOrPut(this._scheduledRegs, key, new Func<Object, ArrayList<Func<Void, Iterable<HandlerRegistration>>>>(){
                    @Override public ArrayList<Func<Void, Iterable<HandlerRegistration>>> apply(Object arg) {
                        return ListUtils.create();
                    }});
    }
    

    private ArrayList<HandlerRegistration> getHandlersList(Object key) {
        return MapUtils.getOrPut(this._regs, key, new Func<Object, ArrayList<HandlerRegistration>>(){
                    @Override public ArrayList<HandlerRegistration> apply(Object arg) {
                        return ListUtils.create();
                    }});
    }
}
