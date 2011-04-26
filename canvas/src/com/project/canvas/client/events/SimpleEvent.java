package com.project.canvas.client.events;

import java.util.ArrayList;

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
			@Override
			public void removeHandler() {
				handlers.remove(handler);
			}
		};
	}
	
	public void dispatch(T arg) {
		for (Handler<T> handler : this.handlers) {
			handler.onFire(arg);
		}
	}
}
