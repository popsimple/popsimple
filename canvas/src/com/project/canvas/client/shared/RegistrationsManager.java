package com.project.canvas.client.shared;

import java.util.ArrayList;

import com.google.gwt.event.shared.HandlerRegistration;

public class RegistrationsManager {
	protected final ArrayList<HandlerRegistration> _regs = new ArrayList<HandlerRegistration>();
	
	public void add(HandlerRegistration reg) {
		if (null != reg) {
			this._regs.add(reg);
		}
	}
	public void clear() {
		for (HandlerRegistration reg : this._regs) {
			reg.removeHandler();
		}
		this._regs.clear();
	}
}
