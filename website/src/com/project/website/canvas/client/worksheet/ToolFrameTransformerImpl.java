package com.project.website.canvas.client.worksheet;

import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.client.utils.ElementUtils;
import com.project.shared.data.Point2D;
import com.project.shared.data.Rectangle;
import com.project.shared.utils.PointUtils;
import com.project.shared.utils.PointUtils.TransformationMode;
import com.project.website.canvas.client.canvastools.base.CanvasToolFrame;
import com.project.website.canvas.client.resources.CanvasResources;
import com.project.website.canvas.client.worksheet.interfaces.ElementDragManager;
import com.project.website.canvas.client.worksheet.interfaces.ToolFrameTransformer;

public class ToolFrameTransformerImpl implements ToolFrameTransformer
{
    // 1 = best, higer value means bigger angle steps (lower resolution)
    private static final int ROTATION_ROUND_RESOLUTION = 3;

    private static final double GRID_RESOLUTION = 15;

    private final Widget _container;
    private final ElementDragManager _elementDragManager;

    public ToolFrameTransformerImpl(Widget container, Widget dragPanel, SimpleEvent<Void> stopOperationEvent)
    {
        _elementDragManager = new ElementDragManagerImpl(container, dragPanel,
                CanvasResources.INSTANCE.main().drag(), stopOperationEvent);
        dragPanel.getElement();
        _container = container;
    }


    @Override
    public ElementDragManager getElementDragManager()
    {
        return _elementDragManager;
    }


    @Override
    public void setToolFramePosition(final CanvasToolFrame toolFrame, Point2D pos)
    {
        ElementUtils.setElementPosition(toolFrame.asWidget().getElement(), limitPosToContainer(pos, toolFrame.asWidget()));
        toolFrame.onTransformed();
    }

    @Override
    public void startDragCanvasToolFrames(Iterable<CanvasToolFrame> toolFrames, MouseEvent<?> startEvent)
    {
    	for (CanvasToolFrame toolFrame : toolFrames)
    	{
    		startDragCanvasToolFrame(toolFrame, startEvent);
    	}
    }

    @Override
    public void startDragCanvasToolFrame(final CanvasToolFrame toolFrame, final MouseEvent<?> startEvent)
    {
        final Point2D initialPos = ElementUtils.getElementOffsetPosition(toolFrame.asWidget().getElement());
        toolFrame.setDragging(true);
        final SimpleEvent.Handler<Point2D> dragHandler = new SimpleEvent.Handler<Point2D>() {
            @Override
            public void onFire(Point2D pos)
            {
                setToolFramePosition(toolFrame, pos); //transformMovement(pos, initialPos, false));
            }
        };
        Handler<Void> cancelMoveHandler = new Handler<Void>() {
            @Override
            public void onFire(Void arg)
            {
                setToolFramePosition(toolFrame, initialPos);
                toolFrame.setDragging(false);
            }
        };

        Handler<Point2D> stopHandler = new Handler<Point2D>() {
            @Override
            public void onFire(Point2D arg)
            {
                toolFrame.setDragging(false);
            }
        };
        _elementDragManager.startMouseMoveOperation(toolFrame.asWidget().getElement(), _container.getElement(),
                Point2D.zero, dragHandler, stopHandler,
                cancelMoveHandler, ElementDragManager.StopCondition.STOP_CONDITION_MOUSE_UP);
    }


    /**
     * Starts (and handles) a resize operation on a CanvasToolFrame. Implementation logic:
     *
     * <el><li>If we change the size of an element when it's rotated around it's center (transform-origin: "") then
     * it's position changes with the size (to keep the center in place). We must therefore switch the element's
     * transform-origin to the top-left corner during resize, and switch back to default (rotate around center) after.</li>
     *
     * <li>Switching the transformation origin while the element is rotate causes the position to jump. So we also need to
     * move the element before the resize and move it back after the resize, so that it will appear in the same position</li>
     *
     * </el>
     */
    @Override
    public void startResizeCanvasToolFrame(final CanvasToolFrame toolFrame, final MouseEvent<?> startEvent)
    {
        final Element toolFrameElement = toolFrame.asWidget().getElement();
        final double angle = Math.toRadians(ElementUtils.getRotation(toolFrameElement));
        final Point2D initialSize = toolFrame.getToolSize();
        final Point2D startDragPos = ElementUtils.relativePosition(startEvent, _container.getElement());

        final Point2D startPos = startDragPos.minus(ElementUtils.relativePosition(startEvent, toolFrameElement));

        final Rectangle initialToolFrameRect = ElementUtils.getElementOffsetRectangle(toolFrameElement);
        final Point2D initialTopLeft = initialToolFrameRect.getCorners().topLeft;

        // Change the rotation axis to the top-left corner, the element will then appear in a different place on the screen
        ElementUtils.setTransformOriginTopLeft(toolFrameElement);

        // Now when we set the element's position, we are setting the position of the top-left corner (the new transform origin).
        // Move the element so that its top-left corner is the same as before switching the rotation axis.
        setToolFramePosition(toolFrame, initialTopLeft);


        final SimpleEvent.Handler<Point2D> resizeHandler = new SimpleEvent.Handler<Point2D>() {
            @Override
            public void onFire(Point2D pos)
            {
                Point2D size = sizeFromRotatedSizeOffset(angle, initialSize, startDragPos, pos);
                toolFrame.setToolSize(transformMovement(size, initialSize));
            }
        };
        final SimpleEvent.Handler<Point2D> stopHandler = new SimpleEvent.Handler<Point2D>() {
            @Override
            public void onFire(Point2D pos)
            {
                Point2D size = sizeFromRotatedSizeOffset(angle, initialSize, startDragPos, pos);
                toolFrame.setToolSize(transformMovement(size, initialSize));

                // Move the rotation axis back to the center of the element (reset the transform origin)
                // The element will jump
                ElementUtils.resetTransformOrigin(toolFrameElement);

                // Move the element so that its top-left is in the same position as it was before the resize.
                Rectangle postResizeToolFrameRect = ElementUtils.getElementOffsetRectangle(toolFrameElement);
                Point2D postResizeTopLeft = postResizeToolFrameRect.getCorners().topLeft;
                setToolFramePosition(toolFrame, ElementUtils.getElementOffsetPosition(toolFrameElement).minus(postResizeTopLeft).plus(initialTopLeft));
            }
        };
        final SimpleEvent.Handler<Void> cancelHandler = new SimpleEvent.Handler<Void>() {
            @Override
            public void onFire(Void arg)
            {
                toolFrame.setToolSize(initialSize);
                ElementUtils.resetTransformOrigin(toolFrameElement);
                setToolFramePosition(toolFrame, startPos);
            }
        };
        _elementDragManager.startMouseMoveOperation(toolFrameElement, _container.getElement(),
                Point2D.zero, resizeHandler, stopHandler, cancelHandler,
                ElementDragManager.StopCondition.STOP_CONDITION_MOUSE_UP);
    }

    @Override
    public void startRotateCanvasToolFrame(final CanvasToolFrame toolFrame, MouseEvent<?> startEvent)
    {
        Rectangle initialRect = ElementUtils.getElementOffsetRectangle(toolFrame.asWidget().getElement());

        Point2D unrotatedBottomLeftRelativeToCenter = initialRect.getSize().mulCoords(-0.5, 0.5);
        final double unrotatedBottomLeftAngle = Math.toDegrees(unrotatedBottomLeftRelativeToCenter.radians());
        final double startAngle = ElementUtils.getRotation(toolFrame.asWidget().getElement());

        final SimpleEvent.Handler<Point2D> rotateHandler = new SimpleEvent.Handler<Point2D>() {
            @Override
            public void onFire(Point2D posRelativeToCenter)
            {
                double rotation = Math.toDegrees(posRelativeToCenter.radians()) - unrotatedBottomLeftAngle;
                ElementUtils.setRotation(toolFrame.asWidget().getElement(), roundedAngle(rotation));
                toolFrame.onTransformed();
            }
        };
        final SimpleEvent.Handler<Void> cancelHandler = new SimpleEvent.Handler<Void>() {
            @Override
            public void onFire(Void arg)
            {
                ElementUtils.setRotation(toolFrame.asWidget().getElement(), startAngle);
                toolFrame.onTransformed();
            }
        };
        _elementDragManager.startMouseMoveOperation(toolFrame.asWidget().getElement(),
                // add a constant so that events' positions will be relative to the element's center
                toolCenterRelativeToToolUnrotatedTopLeft(toolFrame.asWidget()),
                rotateHandler, null, cancelHandler, ElementDragManager.StopCondition.STOP_CONDITION_MOUSE_UP);
    }

    private Point2D sizeFromRotatedSizeOffset(final double angle, final Point2D initialSize,
            final Point2D startDragPos, Point2D pos)
    {
        Point2D rotatedSizeOffset = pos.minus(startDragPos);
        Point2D sizeOffset = rotatedSizeOffset.rotate(-angle);
        Point2D size = Point2D.max(initialSize.plus(sizeOffset), Point2D.zero);
        return size;
    }

    private Point2D toolCenterRelativeToToolUnrotatedTopLeft(final Widget widget)
    {
        Point2D frameSize = new Point2D(widget.getOffsetWidth(), widget.getOffsetHeight());
        Point2D toolCenterPos = frameSize.mul(0.5); // relative to tool top-left
        return toolCenterPos;
    }

    private Point2D limitPosToContainer(Point2D pos, Widget elem)
    {
        Point2D margin = new Point2D(20, 20);
        Point2D maxPos = ElementUtils.getElementOffsetSize(this._container.getElement()).minus(margin);
        Point2D minPos = Point2D.zero.minus(ElementUtils.getElementOffsetSize(elem.getElement()).minus(margin));
        return Point2D.max(minPos, Point2D.min(maxPos, pos));
    }

    private int roundedAngle(double rotation)
    {
        return (int) Math.round(ROTATION_ROUND_RESOLUTION * (rotation / ROTATION_ROUND_RESOLUTION));
    }

    private Point2D transformMovement(Point2D size, Point2D initialCoords)
    {
        return transformMovement(size, initialCoords, true);
    }

    private Point2D transformMovement(Point2D size, Point2D initialCoords, boolean allowMean)
    {
        Point2D sizeDelta = size.minus(initialCoords);
        Event event = Event.getCurrentEvent();
        if (null != event) {
            TransformationMode mode = TransformationMode.NONE;
            if (allowMean && event.getCtrlKey()) {
                mode = TransformationMode.MEAN;
            }
            else if (event.getShiftKey() && event.getAltKey()) {
                // do nothing here.
            }
            else if (event.getShiftKey()) {
                mode = TransformationMode.SNAP_Y;
            }
            else if (event.getAltKey()) {
                mode = TransformationMode.SNAP_X;
            }
            sizeDelta = PointUtils.transform(sizeDelta, mode);
            if (event.getShiftKey() && event.getAltKey()) {
                // Snap to grid.
                sizeDelta = sizeDelta.mul(1/GRID_RESOLUTION).mul(GRID_RESOLUTION);
            }
        }
        return initialCoords.plus(sizeDelta);
    }

}
