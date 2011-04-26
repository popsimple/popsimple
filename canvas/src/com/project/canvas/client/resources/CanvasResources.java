package com.project.canvas.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;

public interface CanvasResources extends ClientBundle {
	public static final CanvasResources INSTANCE =  GWT.create(CanvasResources.class);

	@Source("main.css")
	MainStyles main();
}
