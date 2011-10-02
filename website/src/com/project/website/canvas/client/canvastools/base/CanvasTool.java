package com.project.website.canvas.client.canvastools.base;

import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.project.website.canvas.shared.data.ElementData;

// TODO change getvalue to updateValue to reflect the fact that it mutates the instance of data that was given in the setValue?
public interface CanvasTool<T extends ElementData> extends IsWidget, TakesValue<T> {

    ICanvasToolEvents getToolEvents();

    void setElementData(ElementData data); // non-generic version of setValue

    // Notifies the tool that it became active/inactive in the worksheet.
    void setActive(boolean isActive);

    // Start handling events
    void bind();

    ResizeMode getResizeMode();
    boolean canRotate();

    boolean dimOnLoad();

    void setViewMode(boolean isViewMode);

    //TODO: Replace with a custom Resize event.
    void onResize();

    IsWidget getToolbar();
}
