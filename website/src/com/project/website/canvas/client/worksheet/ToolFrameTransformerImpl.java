package com.project.website.canvas.client.worksheet;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.utils.ElementUtils;
import com.project.shared.data.Point2D;
import com.project.shared.data.Rectangle;
import com.project.shared.utils.PointUtils;
import com.project.shared.utils.PointUtils.ConstraintMode;
import com.project.website.canvas.client.canvastools.base.interfaces.CanvasToolFrame;
import com.project.website.canvas.client.resources.CanvasResources;
import com.project.website.canvas.client.shared.UndoManager;
import com.project.website.canvas.client.shared.UndoManager.UndoRedoPair;
import com.project.website.canvas.client.worksheet.interfaces.ElementDragManager;
import com.project.website.canvas.client.worksheet.interfaces.MouseMoveOperationHandler;
import com.project.website.canvas.client.worksheet.interfaces.ToolFrameTransformer;

public class ToolFrameTransformerImpl implements ToolFrameTransformer
{
    // 1 = best, higer value means bigger angle steps (lower resolution)
    private static final int ROTATION_ROUND_RESOLUTION = 3;

    private static final double GRID_RESOLUTION = 50;

    protected static final int DEFAULT_ANIMATION_DURATION = 300;

    private final Widget _container;
    private final ElementDragManager _elementDragManager;

    private double gridResolution = GRID_RESOLUTION;
    private boolean snapToGrid = false;


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
    public void setToolFramePosition(CanvasToolFrame toolFrame, Point2D pos)
    {
        this.setToolFramePosition(toolFrame, pos, 0);
    }

    @Override
    public void setToolFramePosition(final CanvasToolFrame toolFrame, Point2D pos, int animationDuration)
    {
        ElementUtils.setElementCSSPosition(toolFrame.asWidget().getElement(), pos, animationDuration);
        toolFrame.onTransformed();
    }

    @Override
    public void startDragCanvasToolFrames(Iterable<CanvasToolFrame> toolFrames)
    {
    	for (CanvasToolFrame toolFrame : toolFrames)
    	{
    		startDragCanvasToolFrame(toolFrame);
    	}
    }

    @Override
    public void startDragCanvasToolFrame(final CanvasToolFrame toolFrame)
    {
        final Element toolFrameElement = toolFrame.asWidget().getElement();
        final Point2D initialPos =  ElementUtils.getElementCSSPosition(toolFrameElement);
        final Point2D originalOffsetFromFramePos = ElementUtils.getMousePositionRelativeToElement(RootPanel.getBodyElement())
                                                               .minus(initialPos);

        MouseMoveOperationHandler handler = new MouseMoveOperationHandler() {
            @Override public void onStop(Point2D pos) {
                toolFrame.setDragging(false);
                addDragUndoStep(toolFrame, initialPos, ElementUtils.getElementAbsolutePosition(toolFrameElement));
            }

            @Override public void onStart() {
                toolFrame.setDragging(true);
            }

            @Override public void onMouseMove(Point2D pos) {
                setToolFramePosition(toolFrame, calcDragTargetPos(initialPos, originalOffsetFromFramePos, pos));
            }

            @Override public void onCancel() {
                setToolFramePosition(toolFrame, initialPos);
                toolFrame.setDragging(false);
            }
        };

        _elementDragManager.startMouseMoveOperation(toolFrameElement, RootPanel.getBodyElement(),
                Point2D.zero, handler, ElementDragManager.StopCondition.STOP_CONDITION_MOVEMENT_STOP);
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
    public void startResizeCanvasToolFrame(final CanvasToolFrame toolFrame)
    {
        final Element toolFrameElement = toolFrame.asWidget().getElement();
        final double angle = Math.toRadians(ElementUtils.getRotation(toolFrameElement));
        final Point2D initialSize = toolFrame.getToolSize();
        final Point2D startDragPos = ElementUtils.getMousePositionRelativeToElement(RootPanel.getBodyElement());

        final Point2D startPos = startDragPos.minus(ElementUtils.getMousePositionRelativeToElement(toolFrameElement));

        final Rectangle initialToolFrameRect = ElementUtils.getElementOffsetRectangle(toolFrameElement);
        final Point2D initialTopLeft = initialToolFrameRect.getCorners().topLeft;

        // Change the rotation axis to the top-left corner, the element will then appear in a different place on the screen
        ElementUtils.setTransformOriginTopLeft(toolFrameElement);

        // Now when we set the element's position, we are setting the position of the top-left corner (the new transform origin).
        // Move the element so that its top-left corner is the same as before switching the rotation axis.
        setToolFramePosition(toolFrame, initialTopLeft);

        MouseMoveOperationHandler handler = new MouseMoveOperationHandler() {
            @Override public void onStop(final Point2D pos) {
                UndoManager.get().addAndRedo(toolFrame, new UndoRedoPair() {
                    @Override public void undo() {
                        onCancel();
                    }
                    @Override public void redo() {
                        onResizeFrameFinished(toolFrame, toolFrameElement, angle, initialSize, startDragPos, initialTopLeft, pos);
                    }
                });
            }

            @Override public void onStart() { }

            @Override public void onMouseMove(Point2D pos) {
                Point2D size = sizeFromRotatedSizeOffset(angle, initialSize, startDragPos, pos);
                toolFrame.setToolSize(transformMovement(size, initialSize));
            }

            @Override public void onCancel() {
                toolFrame.setToolSize(initialSize);
                ElementUtils.resetTransformOrigin(toolFrameElement);
                setToolFramePosition(toolFrame, startPos);
            }
        };


        _elementDragManager.startMouseMoveOperation(toolFrameElement, RootPanel.getBodyElement(),
                Point2D.zero, handler, ElementDragManager.StopCondition.STOP_CONDITION_MOVEMENT_STOP);
    }

    @Override
    public void startRotateCanvasToolFrame(final CanvasToolFrame toolFrame)
    {
        Rectangle initialRect = ElementUtils.getElementAbsoluteRectangle(toolFrame.asWidget().getElement());

        final Point2D initialCenter = initialRect.getCenter();
        Point2D unrotatedBottomLeftRelativeToCenter = initialRect.getSize().mulCoords(-0.5, 0.5);
        final double unrotatedBottomLeftAngle = Math.toDegrees(unrotatedBottomLeftRelativeToCenter.getRadians());
        final double startAngle = ElementUtils.getRotation(toolFrame.asWidget().getElement());

        MouseMoveOperationHandler handler = new MouseMoveOperationHandler() {
            @Override public void onStop(final Point2D pos) {
                UndoManager.get().add(toolFrame, new UndoRedoPair() {
                    @Override public void undo() {
                        ElementUtils.setRotation(toolFrame.asWidget().getElement(), startAngle, DEFAULT_ANIMATION_DURATION);
                        toolFrame.onTransformed();
                    }
                    @Override public void redo() {
                        rotateToolFrame(toolFrame, initialCenter, unrotatedBottomLeftAngle, pos, DEFAULT_ANIMATION_DURATION);
                    }
                });
            }

            @Override public void onStart() { }

            @Override public void onMouseMove(Point2D pos) {
                rotateToolFrame(toolFrame, initialCenter, unrotatedBottomLeftAngle, pos, 0);
            }

            @Override public void onCancel() {
                ElementUtils.setRotation(toolFrame.asWidget().getElement(), startAngle);
                toolFrame.onTransformed();
            }
        };

        _elementDragManager.startMouseMoveOperation(toolFrame.asWidget().getElement(), RootPanel.getBodyElement(),
                Point2D.zero, handler, ElementDragManager.StopCondition.STOP_CONDITION_MOVEMENT_STOP);
    }

    private Point2D sizeFromRotatedSizeOffset(final double angle, final Point2D initialSize,
            final Point2D startDragPos, Point2D pos)
    {
        Point2D rotatedSizeOffset = pos.minus(startDragPos);
        Point2D sizeOffset = rotatedSizeOffset.getRotated(-angle);
        Point2D size = Point2D.max(initialSize.plus(sizeOffset), Point2D.zero);
        return size;
    }

    private int roundedAngle(double rotation)
    {
        return (int) Math.round(ROTATION_ROUND_RESOLUTION * (rotation / ROTATION_ROUND_RESOLUTION));
    }

    private Point2D transformMovement(Point2D coords, Point2D initialCoords)
    {
        return transformMovement(coords, initialCoords, true);
    }

    private Point2D transformMovement(Point2D coords, Point2D initialCoords, boolean allowMean)
    {
        Event event = Event.getCurrentEvent();
        if (null == event) {
            return this.applySnapToGrid(coords);
        }
        Point2D sizeDelta = coords.minus(initialCoords);
        ConstraintMode mode = ConstraintMode.NONE;
        if (allowMean && event.getCtrlKey()) {
            mode = ConstraintMode.KEEP_RATIO;
        }
        else if (event.getShiftKey() && event.getAltKey()) {
            // do nothing here.
        }
        else if (event.getShiftKey()) {
            mode = ConstraintMode.SNAP_Y;
        }
        else if (event.getAltKey()) {
            mode = ConstraintMode.SNAP_X;
        }
        sizeDelta = PointUtils.constrain(sizeDelta, initialCoords, mode);
        if (this.snapToGrid || (event.getShiftKey() && event.getAltKey())) {
            sizeDelta = applySnapToGrid(sizeDelta);
        }
        return this.applySnapToGrid(initialCoords.plus(sizeDelta));
    }


    @Override
    public Point2D applySnapToGrid(Point2D sizeDelta)
    {
        if (this.snapToGrid) {
            return sizeDelta.mul(1/gridResolution).mul(gridResolution);
        }
        return sizeDelta;
    }


    private Point2D calcDragTargetPos(final Point2D initialPos, final Point2D originalOffsetFromFramePos, Point2D pos)
    {
        return transformMovement(pos.minus(originalOffsetFromFramePos), initialPos, false);
    }


    private void addDragUndoStep(final CanvasToolFrame toolFrame, final Point2D initialPos, final Point2D targetPos)
    {
        UndoManager.get().add(toolFrame, new UndoRedoPair() {
            @Override public void undo() {
                //Point2D currentPos = ElementUtils.getElementAbsolutePosition(toolFrame.asWidget().getElement());
                //.minus(targetPos).plus(currentPos)
                setToolFramePosition(toolFrame, initialPos, DEFAULT_ANIMATION_DURATION);
            }

            @Override public void redo() {
                setToolFramePosition(toolFrame, targetPos, DEFAULT_ANIMATION_DURATION);
            }
        });
    }


    private void onResizeFrameFinished(final CanvasToolFrame toolFrame, final Element toolFrameElement,
            final double angle, final Point2D initialSize, final Point2D startDragPos, final Point2D initialTopLeft,
            Point2D pos)
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


    private void rotateToolFrame(final CanvasToolFrame toolFrame, final Point2D initialCenter,
            final double unrotatedBottomLeftAngle, Point2D pos, int animationDuration)
    {
        Point2D posRelativeToCenter = pos.minus(initialCenter);
        double rotation = Math.toDegrees(posRelativeToCenter.getRadians()) - unrotatedBottomLeftAngle;
        ElementUtils.setRotation(toolFrame.asWidget().getElement(), roundedAngle(rotation), animationDuration);
        toolFrame.onTransformed();
    }

    @Override
    public double getGridResolution()
    {
        return gridResolution;
    }

    @Override
    public void setGridResolution(double gridResolution)
    {
        this.gridResolution = gridResolution;
    }

    @Override
    public boolean isSnapToGrid()
    {
        return snapToGrid;
    }


    @Override
    public void setSnapToGrid(boolean snapToGrid)
    {
        this.snapToGrid = snapToGrid;
    }
}
