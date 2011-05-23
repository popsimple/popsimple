package com.project.canvas.client.worksheet.interfaces;

import com.google.gwt.dom.client.Element;
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
     * @param referenceElem The element relative to which the position will be calculated
     * @param referenceOffset An additional constant offset added to the position
     * @param moveHandler Called when the mouse moves
     * @param floatingWidgetStop Called when the operation ends
     * @param cancelHandler Called when the operation is cancelled
     * @param stopConditions Conditions for stopping the operation - a bit field of StopCondition values. At least one value must be set.
     * @return a handler which the caller must doFire when it wants to force a stop on the operation
     */
    public abstract SimpleEvent.Handler<Void> startMouseMoveOperation(final Element referenceElem,
            final Point2D referenceOffset, final SimpleEvent.Handler<Point2D> moveHandler,
            final Handler<Point2D> floatingWidgetStop, final SimpleEvent.Handler<Void> cancelHandler, int stopConditions);

}