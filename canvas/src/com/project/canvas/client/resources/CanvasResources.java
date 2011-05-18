package com.project.canvas.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface CanvasResources extends ClientBundle {
	public static final CanvasResources INSTANCE = GWT
			.create(CanvasResources.class);

	@Source("main.css")
	MainStyles main();

	@Source("com/project/canvas/client/resources/images/bitmaps/text_48.png")
	ImageResource toolboxTextIcon();

	@Source("com/project/canvas/client/resources/images/bitmaps/cursor_48.png")
	ImageResource toolboxCursorIcon();

	@Source("com/project/canvas/client/resources/images/bitmaps/tasklist_2_48.png")
	ImageResource toolboxTaskListIcon();

	@Source("com/project/canvas/client/resources/images/bitmaps/image_48.png")
	ImageResource toolboxImageIcon();

	@Source("com/project/canvas/client/resources/images/bitmaps/delete.png")
	ImageResource taskRemoveIcon();

	@Source("com/project/canvas/client/resources/images/bitmaps/resizeIcon.png")
	ImageResource resizeIcon();

	@Source("com/project/canvas/client/resources/images/bitmaps/closeIcon.png")
	ImageResource closeIcon();

	@Source("com/project/canvas/client/resources/images/bitmaps/moveBackIcon.png")
	ImageResource moveBackIcon();

	@Source("com/project/canvas/client/resources/images/bitmaps/moveFrontIcon.png")
	ImageResource moveFrontIcon();
	
	@Source("com/project/canvas/client/resources/images/bitmaps/helpIcon_blue_32.png")
	ImageResource taskDefaultIcon();
}
