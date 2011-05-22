package com.project.canvas.client.worksheet;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
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
        
        _elementDragManager.startMouseMoveOperation(_container.getElement(),
                ElementUtils.relativePosition(startEvent, toolFrame.getElement()), dragHandler, stopMoveHandler,
                cancelMoveHandler, ElementDragManager.StopCondition.STOP_CONDITION_MOUSE_UP);
    }

    Widget label = new Label("HERE");
    
    protected void startResizeCanvasToolFrame(final CanvasToolFrame toolFrame, final MouseEvent<?> startEvent)
    {
        final double angle = Math.toRadians(ElementUtils.getRotation(toolFrame.getElement()));
        final Point2D initialSize = toolFrame.getToolSize();
        final Point2D startDragPos = ElementUtils.relativePosition(startEvent, _container.getElement());
        final Point2D startPos = startDragPos.minus(ElementUtils.relativePosition(startEvent, toolFrame.getElement()));

        ((Panel)_container).remove(label);
        ((Panel)_container).add(label);
        label.getElement().getStyle().setZIndex(998);
        label.getElement().getStyle().setPosition(Position.ABSOLUTE);
        ElementUtils.setElementPosition(startPos, label.getElement());
        
        final SimpleEvent.Handler<Point2D> resizeHandler = new SimpleEvent.Handler<Point2D>() {
            @Override
            public void onFire(Point2D pos)
            {
            	Point2D rotatedSizeOffset = pos.minus(startDragPos);
            	Point2D sizeOffset = rotatedSizeOffset.rotate(-angle);
            	Point2D size = Point2D.max(initialSize.plus(sizeOffset), Point2D.zero);
                toolFrame.setToolSize(size);
                Point2D center = size.mul(0.5);
                setToolFramePosition(toolFrame, startPos.plus(center.rotate(angle)).minus(center));
            }
        };
        final SimpleEvent.Handler<Void> cancelHandler = new SimpleEvent.Handler<Void>() {
            @Override
            public void onFire(Void arg)
            {
                toolFrame.setToolSize(initialSize);
            }
        };
        _elementDragManager.startMouseMoveOperation(_container.getElement(), 
        		Point2D.zero, resizeHandler, null, cancelHandler, 
        		ElementDragManager.StopCondition.STOP_CONDITION_MOUSE_UP);
    }

    protected void startRotateCanvasToolFrame(final CanvasToolFrame toolFrame, MouseEvent<?> startEvent)
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

	private Point2D toolCenterRelativeToToolTopLeft(final Widget widget) {
		Point2D frameSize = new Point2D(widget.getOffsetWidth(), widget.getOffsetHeight());
        Point2D toolCenterPos = frameSize.mul(0.5); // relative to tool top-left
		return toolCenterPos;
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
