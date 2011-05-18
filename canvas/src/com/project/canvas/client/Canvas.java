package com.project.canvas.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.RootPanel;
import com.project.canvas.client.resources.CanvasResources;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Canvas implements EntryPoint {
	
	CanvasContainer canvasContainer = new CanvasContainer();
	
	public void onModuleLoad() {
		CanvasResources.INSTANCE.main().ensureInjected();
//		TextEditTool.ensureResourcesLoaded();
		
		RootPanel.get("root").add(this.canvasContainer);

		
		History.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				canvasContainer.getWorksheet().load(event.getValue());
			}
		});
		History.fireCurrentHistoryState();
		
		

	}
}
