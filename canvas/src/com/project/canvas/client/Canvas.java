package com.project.canvas.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Canvas implements EntryPoint {
	
	CanvasContainer canvasContainer = new CanvasContainer();
	
	public void onModuleLoad() {
		RootPanel.get("root").add(this.canvasContainer);
		
	}
}
