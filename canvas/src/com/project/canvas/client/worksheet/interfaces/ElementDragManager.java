package com.project.canvas.client.worksheet.interfaces;

import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.user.client.Element;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.client.shared.events.SimpleEvent.Handler;
import com.project.canvas.shared.data.Point2D;

public interface ElementDragManager
{

    public class StopCondition {
        public static final int STOP_CONDITION_MOUSE_UP = 1;
        public static final int STOP_CONDITION_MOUSE_CLICK = 2;
    }

    /**
     * Starts a mouse move/drag operation. Takes care of placing a drag panel on top of all others to capture events, etc.
     * @param targetElement The element that initially started the operation
     * @param referenceElem The element relative to which the position will be calculated
     * @param referenceOffset An additional constant offset added to the position
     * @param moveHandler Called when the mouse moves
     * @param stopHandler Called when the operation ends
     * @param cancelHandler Called when the operation is cancelled
     * @param stopConditions Conditions for stopping the operation - a bit field of StopCondition values. At least one value must be set.
     * @param dragStyleName the style name to apply to the target element while dragging
     * @return a handler which the caller must doFire when it wants to force a stop on the operation
     */
    SimpleEvent.Handler<Void> startMouseMoveOperation(
            final Element targetElement, final Element referenceElem,
            final Point2D referenceOffset, final SimpleEvent.Handler<Point2D> moveHandler,
            final Handler<Point2D> stopHandler, final SimpleEvent.Handler<Void> cancelHandler,
            int stopConditions);

    /**
     * Starts a mouse move/drag operation. Takes care of placing a drag panel on top of all others to capture events, etc.
     * @param targetElement The element that initially started the operation
     * @param referenceOffset An additional constant offset added to the position
     * @param moveHandler Called when the mouse moves
     * @param stopHandler Called when the operation ends
     * @param cancelHandler Called when the operation is cancelled
     * @param stopConditions Conditions for stopping the operation - a bit field of StopCondition values. At least one value must be set.
     * @param dragStyleName the style name to apply to the target element while dragging
     * @return a handler which the caller must doFire when it wants to force a stop on the operation
     */
    SimpleEvent.Handler<Void> startMouseMoveOperation(final Element targetElement,
            final Point2D referenceOffset, final SimpleEvent.Handler<Point2D> moveHandler,
            final Handler<Point2D> stopHandler, final SimpleEvent.Handler<Void> cancelHandler,
            int stopConditions);
}