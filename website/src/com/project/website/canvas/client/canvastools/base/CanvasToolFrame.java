package com.project.website.canvas.client.canvastools.base;

import com.google.gwt.event.dom.client.HasBlurHandlers;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Focusable;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.data.Point2D;
import com.project.website.canvas.shared.data.ElementData;

public interface CanvasToolFrame extends Focusable, HasFocusHandlers, HasBlurHandlers
{

     void setViewMode(boolean inViewMode);

     CanvasTool<? extends ElementData> getTool();

     HandlerRegistration addCloseRequestHandler(SimpleEvent.Handler<Void> handler);

     HandlerRegistration addMoveStartRequestHandler(SimpleEvent.Handler<MouseEvent<?>> handler);

     HandlerRegistration addMoveBackRequestHandler(SimpleEvent.Handler<Void> handler);

     HandlerRegistration addMoveFrontRequestHandler(SimpleEvent.Handler<Void> handler);

     HandlerRegistration addResizeStartRequestHandler(SimpleEvent.Handler<MouseEvent<?>> handler);

     HandlerRegistration addRotateStartRequestHandler(SimpleEvent.Handler<MouseEvent<?>> handler);

     HandlerRegistration addMouseDownHandler(MouseDownHandler handler);

     HandlerRegistration addMouseUpHandler(MouseUpHandler handler);

     Point2D getToolSize();

    /**
     * Note: make sure the size here does NOT include padding/margin/border of the tool, otherwise
     * getToolSize and setToolSize will not be compatible (will be using different values.)
     */
     void setToolSize(Point2D size);

    /**
     * Notifies the CanvasToolFrame that it is being dragged / not being dragged.
     * The dragged state is actually a stack, so that if several different mechanisms
     * want the frame to think it's being dragged, it will prevent one of them from
     * turning off the drag state by mistake while the frame is still being considered dragged
     * by another mechanism.
     * This is used for knowing whether we should pass setActive commands in to the CanvasTool.
     * @param isDragging
     */
     void setDragging(boolean isDragging);

    /**
     * Wraps CanvasTool.setActive so that if the tool frame is being dragged,
     * it will not be set active until the operation ends.
     * This is REQUIRED: because if the tool steals focus when it becomes active,
     * the drag manager in the worksheet may receive a stop event immediately.
     * @param isActive
     */
     void setActive(boolean isActive);

     void onTransformed();
}