package com.project.website.canvas.client.canvastools.base;

import com.google.gwt.user.client.ui.Widget;
import com.project.shared.data.Point2D;
import com.project.shared.interfaces.Factory;

public interface CanvasToolFactory<T extends CanvasTool<?>> extends Factory<T>
{
    boolean isOneShot();

    Widget getFloatingWidget();

    Point2D getCreationOffset();

    String getFactoryId();
}
