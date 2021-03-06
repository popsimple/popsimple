package com.project.website.canvas.client.canvastools.base;

import com.google.gwt.user.client.ui.Widget;
import com.project.shared.data.Point2D;
import com.project.website.canvas.client.canvastools.base.interfaces.CanvasTool;
import com.project.website.canvas.client.canvastools.base.interfaces.CanvasToolFactory;

public abstract class CanvasToolFactoryBase<T extends CanvasTool<?>> implements CanvasToolFactory<T> {
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
        return Point2D.zero;
    }

    @Override
    public Point2D getFloatingWidgetCreationOffset()
    {
        return this.getCreationOffset();
    }


}
