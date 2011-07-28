package com.project.shared.client.events;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Acts like a regular SimpleEvent until the first dispatch.
 * After the first dispatch, immediately fires any handlers added to it.
 */
public class SingleEvent<T> extends SimpleEvent<T> {
	private boolean isDispatched = false;
	private T dispatchedArg = null;
	
	@Override
	public HandlerRegistration addHandler(final Handler<T> handler) {
		if (isDispatched) {
			handler.onFire(this.dispatchedArg);
			return null;
		}
		return super.addHandler(handler);
    }

    public void dispatch(T arg) {
    	this.dispatchedArg = arg;
    	this.isDispatched = true;
    	super.dispatch(arg);
    	this.handlers.clear();
    }
}