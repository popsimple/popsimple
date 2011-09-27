package com.project.website.canvas.client.worksheet.interfaces;

import com.google.gwt.user.client.Element;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.data.Point2D;

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
            final Point2D referenceOffset, MouseMoveOperationHandler handler,
            int stopConditions);

    /**
     * Starts a mouse move/drag operation. Takes care of placing a drag panel on top of all others to capture events, etc.
     * @param targetElement The element that initially started the operation. The position will be calculated relative to this element's position.
     * @param referenceOffset A constant offset added to the position
     * @param moveHandler Called when the mouse moves
     * @param stopHandler Called when the operation ends
     * @param cancelHandler Called when the operation is cancelled
     * @param stopConditions Conditions for stopping the operation - a bit field of StopCondition values. At least one value must be set.
     * @param dragStyleName the style name to apply to the target element while dragging
     * @return a handler which the caller must doFire when it wants to force a stop on the operation
     */
    SimpleEvent.Handler<Void> startMouseMoveOperation(final Element targetElement,
            final Point2D referenceOffset, MouseMoveOperationHandler handler,
            int stopConditions);
}