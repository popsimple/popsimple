package com.project.shared.client.events;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;

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

    public static ClickHandler asClickHandler(final Handler<Void> handler) {
    	return new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				handler.onFire(null);
			}};
    }
}
