package com.project.canvas.client.canvastools.base;

import com.google.gwt.user.client.ui.Widget;
import com.project.canvas.client.shared.WidgetUtils;

public abstract class CanvasToolCommon {

	public static void initCanvasToolWidget(Widget widget) {
		WidgetUtils.stopClickPropagation(widget);
	}

}
