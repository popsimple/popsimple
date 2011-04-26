package com.project.canvas.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.impl.WindowImplIE.Resources;
import com.google.gwt.user.client.ui.RootPanel;
import com.project.canvas.client.resources.CanvasResources;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Canvas implements EntryPoint {
	
	CanvasContainer canvasContainer = new CanvasContainer();
	
	public void onModuleLoad() {
		CanvasResources.INSTANCE.main().ensureInjected();
		RootPanel.get("root").add(this.canvasContainer);
	}
}
