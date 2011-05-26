package com.project.canvas.client.canvastools.base;

import com.google.gwt.user.client.ui.Widget;
import com.project.canvas.shared.Factory;
import com.project.canvas.shared.data.Point2D;

public interface CanvasToolFactory<T extends CanvasTool<?>> extends Factory<T>
{
    boolean isOneShot();

    Widget getFloatingWidget();

    Point2D getCreationOffset();
}
