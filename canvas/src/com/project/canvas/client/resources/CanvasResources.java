package com.project.canvas.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface CanvasResources extends ClientBundle {
	public static final CanvasResources INSTANCE =  GWT.create(CanvasResources.class);

	@Source("main.css")
	MainStyles main();

	@Source("com/project/canvas/client/resources/images/bitmaps/a.png")
	ImageResource toolboxTextIcon();
	
	@Source("com/project/canvas/client/resources/images/bitmaps/cursor.png")
	ImageResource toolboxCursorIcon();
	
	@Source("com/project/canvas/client/resources/images/bitmaps/tasklist.png")
	ImageResource toolboxTaskListIcon();
	
	@Source("com/project/canvas/client/resources/images/bitmaps/image.png")
	ImageResource toolboxImageIcon();
	
	@Source("com/project/canvas/client/resources/images/bitmaps/delete.png")
	ImageResource taskListRemoveIcon();
}
