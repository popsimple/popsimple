package com.project.canvas.client.canvastools.base;

import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.shared.data.ElementData;

// TODO change getvalue to updateValue to reflect the fact that it mutates the instance of data that was given in the setValue?
public interface CanvasTool<T extends ElementData> extends IsWidget, TakesValue<T> {
    SimpleEvent<String> getKillRequestedEvent();

    // tool wants to be dragged around with the mouse
    HandlerRegistration addMoveStartEventHandler(SimpleEvent.Handler<MouseEvent<?>> handler);

    void setElementData(ElementData data); // non-generic version of setValue

    // Notifies the tool that it became active/inactive in the worksheet.
    void setActive(boolean isActive);

    // Start handling events
    void bind();
}
