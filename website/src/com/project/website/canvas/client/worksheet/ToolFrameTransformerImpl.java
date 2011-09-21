package com.project.website.canvas.client.worksheet;

import java.util.Collection;

import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.client.utils.ElementUtils;
import com.project.shared.data.Point2D;
import com.project.shared.utils.PointUtils;
import com.project.shared.utils.PointUtils.TransformationMode;
import com.project.website.canvas.client.canvastools.base.CanvasToolFrameImpl;
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
    public void setToolFramePosition(final CanvasToolFrameImpl toolFrame, Point2D pos)
    {
        ElementUtils.setElementPosition(toolFrame.getElement(), limitPosToContainer(pos, toolFrame));
    }

    @Override
    public void startDragCanvasToolFrames(Collection<CanvasToolFrameImpl> toolFrames, MouseEvent<?> startEvent)
    {
    	for (CanvasToolFrameImpl toolFrame : toolFrames)
    	{
    		startDragCanvasToolFrame(toolFrame, startEvent);
    	}
    }

    @Override
    public void startDragCanvasToolFrame(final CanvasToolFrameImpl toolFrame, final MouseEvent<?> startEvent)
    {
        final Point2D initialPos = ElementUtils.getElementOffsetPosition(toolFrame.getElement());
        toolFrame.setDragging(true);
        final SimpleEvent.Handler<Point2D> dragHandler = new SimpleEvent.Handler<Point2D>() {
            @Override
            public void onFire(Point2D pos)
            {
                setToolFramePosition(toolFrame, transformMovement(pos, initialPos, false));
            }
        };
        Handler<Void> cancelMoveHandler = new Handler<Void>() {
            @Override
            public void onFire(Void arg)
            {
                toolFrame.setDragging(false);
                setToolFramePosition(toolFrame, initialPos);
            }
        };

        Handler<Point2D> stopHandler = new Handler<Point2D>() {
            @Override
            public void onFire(Point2D arg)
            {
                toolFrame.setDragging(false);
            }
        };
        _elementDragManager.startMouseMoveOperation(toolFrame.getElement(), _container.getElement(),
                ElementUtils.relativePosition(startEvent, toolFrame.getElement()), dragHandler, stopHandler,
                cancelMoveHandler, ElementDragManager.StopCondition.STOP_CONDITION_MOUSE_UP);
    }


    @Override
    public void startResizeCanvasToolFrame(final CanvasToolFrameImpl toolFrame, final MouseEvent<?> startEvent)
    {
        final double angle = Math.toRadians(ElementUtils.getRotation(toolFrame.getElement()));
        final Point2D initialSize = toolFrame.getToolSize();
        final Point2D initialFrameSize = ElementUtils.getElementOffsetSize(toolFrame.getElement());
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
                toolFrame.setToolSize(transformMovement(size, initialSize));
            }
        };
        final SimpleEvent.Handler<Point2D> stopHandler = new SimpleEvent.Handler<Point2D>() {
            @Override
            public void onFire(Point2D pos)
            {
                Point2D size = sizeFromRotatedSizeOffset(angle, initialSize, startDragPos, pos);
                toolFrame.setToolSize(transformMovement(size, initialSize));
                ElementUtils.resetTransformOrigin(toolFrame.getElement());
                Point2D frameSize = ElementUtils.getElementOffsetSize(toolFrame.getElement());
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
        _elementDragManager.startMouseMoveOperation(toolFrame.getElement(),
                _container.getElement(), Point2D.zero, resizeHandler, stopHandler,
                cancelHandler, ElementDragManager.StopCondition.STOP_CONDITION_MOUSE_UP);
    }


    @Override
    public void startRotateCanvasToolFrame(final CanvasToolFrameImpl toolFrame, MouseEvent<?> startEvent)
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
        _elementDragManager.startMouseMoveOperation(toolFrame.getElement(),
                toolCenterPos, rotateHandler, null,
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

    private Point2D limitPosToContainer(Point2D pos, Widget elem)
    {
        Point2D margin = new Point2D(20, 20);
        Point2D maxPos = ElementUtils.getElementOffsetSize(this._container.getElement()).minus(margin);
        Point2D minPos = Point2D.zero.minus(ElementUtils.getElementOffsetSize(elem.getElement()).minus(margin));
        return Point2D.max(minPos, Point2D.min(maxPos, pos));
    }

    private int roundedAngle(int rotation)
    {
        return ROTATION_ROUND_RESOLUTION * (rotation / ROTATION_ROUND_RESOLUTION);
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
