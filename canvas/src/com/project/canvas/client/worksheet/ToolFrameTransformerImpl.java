package com.project.canvas.client.worksheet;

import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.user.client.ui.Widget;
import com.project.canvas.client.canvastools.base.CanvasToolFrame;
import com.project.canvas.client.resources.CanvasResources;
import com.project.canvas.client.shared.ElementUtils;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.client.shared.events.SimpleEvent.Handler;
import com.project.canvas.client.worksheet.interfaces.ElementDragManager;
import com.project.canvas.client.worksheet.interfaces.ToolFrameTransformer;
import com.project.canvas.shared.data.Point2D;

public class ToolFrameTransformerImpl implements ToolFrameTransformer
{
    // 1 = best, higer value means bigger angle steps (lower resolution)
    private static final int ROTATION_ROUND_RESOLUTION = 3;

    private final Widget _container;
    private final ElementDragManager _elementDragManager;

    public ToolFrameTransformerImpl(Widget container, Widget dragPanel, SimpleEvent<Void> stopOperationEvent)
    {
        _elementDragManager = new ElementDragManagerImpl(container, dragPanel, stopOperationEvent);
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
        ElementUtils.setElementPosition(limitPosToContainer(pos, toolFrame), toolFrame.getElement());
    }


    @Override
    public void startDragCanvasToolFrame(final CanvasToolFrame toolFrame, final MouseEvent<?> startEvent)
    {
        final SimpleEvent.Handler<Point2D> dragHandler = new SimpleEvent.Handler<Point2D>() {
            @Override
            public void onFire(Point2D pos)
            {
                setToolFramePosition(toolFrame, pos);
            }
        };
        SimpleEvent.Handler<Point2D> stopMoveHandler = new SimpleEvent.Handler<Point2D>() {
            @Override
            public void onFire(Point2D arg)
            {
                setToolFrameDragStyles(toolFrame, false);
            }
        };
        Handler<Void> cancelMoveHandler = new Handler<Void>() {
            @Override
            public void onFire(Void arg)
            {
                setToolFrameDragStyles(toolFrame, false);
            }
        };

        setToolFrameDragStyles(toolFrame, true);

        _elementDragManager.startMouseMoveOperation(_container.getElement(),
                ElementUtils.relativePosition(startEvent, toolFrame.getElement()), dragHandler, stopMoveHandler,
                cancelMoveHandler, ElementDragManager.StopCondition.STOP_CONDITION_MOUSE_UP);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.project.canvas.client.worksheet.ToolFrameTransformer#
     * startResizeCanvasToolFrame
     * (com.project.canvas.client.canvastools.base.CanvasToolFrame,
     * com.google.gwt.event.dom.client.MouseEvent)
     */
    @Override
    public void startResizeCanvasToolFrame(final CanvasToolFrame toolFrame, final MouseEvent<?> startEvent)
    {
        final double angle = Math.toRadians(ElementUtils.getRotation(toolFrame.getElement()));
        final Point2D initialSize = toolFrame.getToolSize();
        final Point2D initialFrameSize = ElementUtils.getElementSize(toolFrame.getElement());
        final Point2D startDragPos = ElementUtils.relativePosition(startEvent, _container.getElement());
        final Point2D startPos = startDragPos.minus(ElementUtils.relativePosition(startEvent, toolFrame.getElement()));
        Point2D initialCenter = initialFrameSize.mul(0.5);
        final Point2D tempPos = startPos.rotate(angle, initialCenter, true);

        ElementUtils.setTransformOriginTopLeft(toolFrame.getElement());
        setToolFramePosition(toolFrame, tempPos);

        final SimpleEvent.Handler<Point2D> resizeHandler = new SimpleEvent.Handler<Point2D>() {
            @Override
            public void onFire(Point2D pos)
            {
                Point2D size = sizeFromRotatedSizeOffset(angle, initialSize, startDragPos, pos);
                toolFrame.setToolSize(size);
            }
        };
        final SimpleEvent.Handler<Point2D> stopHandler = new SimpleEvent.Handler<Point2D>() {
            @Override
            public void onFire(Point2D pos)
            {
                Point2D size = sizeFromRotatedSizeOffset(angle, initialSize, startDragPos, pos);
                toolFrame.setToolSize(size);
                ElementUtils.resetTransformOrigin(toolFrame.getElement());
                Point2D frameSize = ElementUtils.getElementSize(toolFrame.getElement());
                Point2D center = frameSize.mul(0.5);
                // Move the element back to the origin position, taking the new
                // size into account.
                setToolFramePosition(toolFrame, tempPos.rotate(angle, center, false));
            }
        };
        final SimpleEvent.Handler<Void> cancelHandler = new SimpleEvent.Handler<Void>() {
            @Override
            public void onFire(Void arg)
            {
                toolFrame.setToolSize(initialSize);
                ElementUtils.resetTransformOrigin(toolFrame.getElement());
                setToolFramePosition(toolFrame, startPos);
            }
        };
        _elementDragManager.startMouseMoveOperation(_container.getElement(), Point2D.zero, resizeHandler, stopHandler,
                cancelHandler, ElementDragManager.StopCondition.STOP_CONDITION_MOUSE_UP);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.project.canvas.client.worksheet.ToolFrameTransformer#
     * startRotateCanvasToolFrame
     * (com.project.canvas.client.canvastools.base.CanvasToolFrame,
     * com.google.gwt.event.dom.client.MouseEvent)
     */
    @Override
    public void startRotateCanvasToolFrame(final CanvasToolFrame toolFrame, MouseEvent<?> startEvent)
    {
        Point2D toolCenterPos = toolCenterRelativeToToolTopLeft(toolFrame);
        Point2D bottomLeftRelativeToCenter = new Point2D(-toolCenterPos.getX(), toolCenterPos.getY());
        final int bottomLeftAngle = (int) Math.toDegrees(bottomLeftRelativeToCenter.radians());
        final int startAngle = ElementUtils.getRotation(toolFrame.getElement());

        final SimpleEvent.Handler<Point2D> rotateHandler = new SimpleEvent.Handler<Point2D>() {
            @Override
            public void onFire(Point2D posRelativeToCenter)
            {
                int rotation = (int) Math.toDegrees(posRelativeToCenter.radians()) - bottomLeftAngle;
                ElementUtils.setRotation(toolFrame.getElement(), roundedAngle(rotation));
            }
        };
        final SimpleEvent.Handler<Void> cancelHandler = new SimpleEvent.Handler<Void>() {
            @Override
            public void onFire(Void arg)
            {
                ElementUtils.setRotation(toolFrame.getElement(), startAngle);
            }
        };
        _elementDragManager.startMouseMoveOperation(toolFrame.getElement(), toolCenterPos, rotateHandler, null,
                cancelHandler, ElementDragManager.StopCondition.STOP_CONDITION_MOUSE_UP);
    }

    private Point2D sizeFromRotatedSizeOffset(final double angle, final Point2D initialSize,
            final Point2D startDragPos, Point2D pos)
    {
        Point2D rotatedSizeOffset = pos.minus(startDragPos);
        Point2D sizeOffset = rotatedSizeOffset.rotate(-angle);
        Point2D size = Point2D.max(initialSize.plus(sizeOffset), Point2D.zero);
        return size;
    }

    private Point2D toolCenterRelativeToToolTopLeft(final Widget widget)
    {
        Point2D frameSize = new Point2D(widget.getOffsetWidth(), widget.getOffsetHeight());
        Point2D toolCenterPos = frameSize.mul(0.5); // relative to tool top-left
        return toolCenterPos;
    }

    protected Point2D limitPosToContainer(Point2D pos, Widget elem)
    {
        Point2D margin = new Point2D(20, 20);
        Point2D maxPos = ElementUtils.getElementSize(this._container.getElement()).minus(margin);
        Point2D minPos = Point2D.zero.minus(ElementUtils.getElementSize(elem.getElement()).minus(margin));
        return Point2D.max(minPos, Point2D.min(maxPos, pos));
    }

    protected int roundedAngle(int rotation)
    {
        return ROTATION_ROUND_RESOLUTION * (rotation / ROTATION_ROUND_RESOLUTION);
    }

    protected void setToolFrameDragStyles(final CanvasToolFrame toolFrame, boolean dragging)
    {
        if (dragging) {
            toolFrame.addStyleName(CanvasResources.INSTANCE.main().hover());
            toolFrame.addStyleName(CanvasResources.INSTANCE.main().drag());
        } else {
            toolFrame.removeStyleName(CanvasResources.INSTANCE.main().hover());
            toolFrame.removeStyleName(CanvasResources.INSTANCE.main().drag());
        }
    }

}
