package com.project.canvas.client.canvastools.base;

import com.google.gwt.user.client.ui.Widget;
import com.project.canvas.shared.data.Point2D;

public abstract class CanvasToolFactoryBase<T extends CanvasTool<?>>
		implements
			CanvasToolFactory<T> {
	@Override
	public boolean isOneShot() {
		return true;
	}

	private Widget _floatingWidget = null;

	@Override
	public Widget getFloatingWidget() {
		// default implementation is to create a static instance for all
		// floating widget usage
		if (null == _floatingWidget) {
			_floatingWidget = this.create().asWidget();
		}
		return _floatingWidget;
	}

	@Override
	public Point2D getCreationOffset() {
		return new Point2D(0, 0);
	}
}
