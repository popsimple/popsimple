package com.project.canvas.client.worksheet;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.user.client.ui.Widget;
import com.project.canvas.client.canvastools.base.CanvasToolFrame;
import com.project.canvas.client.resources.CanvasResources;
import com.project.canvas.client.shared.ElementUtils;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.client.shared.events.SimpleEvent.Handler;
import com.project.canvas.shared.data.Point2D;

public class ToolFrameTransformer
{
    // 1 = best, higer value means bigger angle steps (lower resolution)
    private static final int ROTATION_ROUND_RESOLUTION = 3;

    private final ElementDragManager _elementDragManager;
    private final Element _dragPanelElement;
    private final Widget _container;

    public ToolFrameTransformer(Widget container, Widget dragPanel, SimpleEvent<Void> stopOperationEvent)
    {
        _elementDragManager = new ElementDragManager(container, dragPanel, stopOperationEvent);
        _dragPanelElement = dragPanel.getElement();
        _container = container;
    }

    public ElementDragManager getElementDragManager()
    {
        return _elementDragManager;
    }

    public Point2D limitPosToContainer(Point2D pos, Widget elem)
    {
        Point2D maxPos = new Point2D(this._container.getOffsetWidth() - 20, this._container.getOffsetHeight() - 20);

        return Point2D.max(Point2D.zero, Point2D.min(maxPos, pos));
    }

    public void setToolFramePosition(final CanvasToolFrame toolFrame, Point2D pos)
    {
        ElementUtils.setElementPosition(limitPosToContainer(pos, toolFrame), toolFrame.getElement());
    }

    protected void startDragCanvasToolFrame(final CanvasToolFrame toolFrame, final MouseEvent<?> startEvent)
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
        
        _elementDragManager.startMouseMoveOperation(_dragPanelElement,
                ElementUtils.relativePosition(startEvent, toolFrame.getElement()), dragHandler, stopMoveHandler,
                cancelMoveHandler, ElementDragManager.StopCondition.STOP_CONDITION_MOUSE_UP);
    }

    protected void startResizeCanvasToolFrame(final CanvasToolFrame toolFrame, final MouseEvent<?> startEvent)
    {
        final Point2D initialSize = toolFrame.getToolSize();

        final SimpleEvent.Handler<Point2D> resizeHandler = new SimpleEvent.Handler<Point2D>() {
            @Override
            public void onFire(Point2D size)
            {
                toolFrame.setToolSize(size);
            }
        };
        final SimpleEvent.Handler<Void> cancelHandler = new SimpleEvent.Handler<Void>() {
            @Override
            public void onFire(Void arg)
            {
                toolFrame.setToolSize(initialSize);
            }
        };
        final Element toolElement = toolFrame.getTool().asWidget().getElement();
        _elementDragManager.startMouseMoveOperation(toolElement, ElementUtils.relativePosition(startEvent, toolElement)
                .minus(initialSize), resizeHandler, null, cancelHandler, ElementDragManager.StopCondition.STOP_CONDITION_MOUSE_UP);
    }

    protected void startRotateCanvasToolFrame(final CanvasToolFrame toolFrame, MouseEvent<?> arg)
    {
        Point2D frameSize = new Point2D(toolFrame.getOffsetWidth(), toolFrame.getOffsetHeight());
        Point2D toolCenterPos = frameSize.mul(0.5); // relative to tool top-left
        Point2D startEventRelativeToTopLeft = ElementUtils.relativePosition(arg, toolFrame.getElement());
        Point2D startEventRelativeToCenter = startEventRelativeToTopLeft.minus(toolCenterPos);
        Point2D bottomLeftRelativeToCenter = new Point2D(-toolCenterPos.getX(), toolCenterPos.getY());
        final int bottomLeftAngle = (int) Math.toDegrees(bottomLeftRelativeToCenter.radians());
        final int startAngle = roundedAngle((int) Math.toDegrees(startEventRelativeToCenter.radians())
                - bottomLeftAngle);

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

    protected int roundedAngle(int rotation)
    {
        return ROTATION_ROUND_RESOLUTION * (rotation / ROTATION_ROUND_RESOLUTION);
    }

    public void setToolFrameDragStyles(final CanvasToolFrame toolFrame, boolean dragging)
    {
        if (dragging) {
            toolFrame.addStyleName(CanvasResources.INSTANCE.main().hover());
            toolFrame.addStyleName(CanvasResources.INSTANCE.main().drag());
        }
        else {
            toolFrame.removeStyleName(CanvasResources.INSTANCE.main().hover());
            toolFrame.removeStyleName(CanvasResources.INSTANCE.main().drag());
        }
    }

}
