package com.project.shared.client.events;

import java.util.ArrayList;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.project.shared.data.funcs.Func;

public class SimpleEvent<T> {
    public interface Handler<T> extends EventHandler {
        void onFire(T arg);
    }

    ArrayList<Handler<T>> handlers = new ArrayList<Handler<T>>();

    public HandlerRegistration addHandler(final Handler<T> handler) {
        this.handlers.add(handler);
        return new HandlerRegistration() {
            public void removeHandler() {
                handlers.remove(handler);
            }
        };
    }

    public void dispatch(T arg) {
        // Iterate a copy to prevent ConcurrentModificationException
        for (Handler<T> handler : new ArrayList<Handler<T>>(this.handlers)) {
            handler.onFire(arg);
        }
    }

    /**
     * Convenience method for using event firing in an AsyncFunc / Func chain.
     * @return An action that dispatches (fires) the event.
     */
	public Func.Action<T> getDispatchFunc() {
		return new Func.Action<T>() {
			@Override
			public void exec(T arg) {
				dispatch(arg);
			}
		};
	}
}
