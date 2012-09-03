package com.project.shared.client.utils.widgets;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.project.shared.client.handlers.RegistrationsManager;
import com.project.shared.client.utils.ZIndexAllocator;

public class DialogWithZIndex extends DialogBox {
	private final RegistrationsManager registrationsManager = new RegistrationsManager();
	
    public DialogWithZIndex(boolean autoHide, boolean modal) {
        super(autoHide, modal);
        super.setGlassEnabled(true);
    }

    @Override
    protected void onLoad()
    {
    	final DialogWithZIndex that = this;
    	super.onLoad();
    	this.registrationsManager.add(Window.addResizeHandler(new ResizeHandler() {
			@Override public void onResize(ResizeEvent event) {
				that.center();
			}}));
    }
    
    @Override
	protected void onUnload()
    {
    	this.registrationsManager.clear();
    	super.onUnload();
    }

    
    
    @Override
    public void show() {
        ZIndexAllocator.allocateSetZIndex(super.getGlassElement());
        ZIndexAllocator.allocateSetZIndex(super.getElement());
        super.show();
    }
}
