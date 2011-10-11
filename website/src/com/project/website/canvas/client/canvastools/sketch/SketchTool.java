package com.project.website.canvas.client.canvastools.sketch;

import com.google.common.base.Strings;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.Composite;
import com.google.gwt.canvas.dom.client.Context2d.LineCap;
import com.google.gwt.canvas.dom.client.Context2d.LineJoin;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.HumanInputEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.client.handlers.RegistrationsManager;
import com.project.shared.client.utils.CanvasUtils;
import com.project.shared.client.utils.ElementUtils;
import com.project.shared.client.utils.EventUtils;
import com.project.shared.client.utils.widgets.WidgetUtils;
import com.project.shared.data.Pair;
import com.project.shared.data.Point2D;
import com.project.shared.data.Rectangle;
import com.project.shared.utils.PointUtils;
import com.project.shared.utils.loggers.Logger;
import com.project.website.canvas.client.canvastools.base.CanvasToolEvents;
import com.project.website.canvas.client.canvastools.base.ResizeMode;
import com.project.website.canvas.client.canvastools.base.interfaces.CanvasTool;
import com.project.website.canvas.client.canvastools.base.interfaces.ICanvasToolEvents;
import com.project.website.canvas.client.resources.CanvasResources;
import com.project.website.canvas.client.shared.UndoManager;
import com.project.website.canvas.shared.data.ElementData;
import com.project.website.canvas.shared.data.SketchData;
import com.project.website.canvas.shared.data.SketchOptions;

public class SketchTool extends FlowPanel implements CanvasTool<SketchData>
{
    private static final int PATH_TERMINATION_ON_MOUSE_OUT_DELAY_MSECS = 500;

    public enum SpiroCurveType {
        Sine("Wave"),
        Circle("Curl");

        String friendlyName;

        private SpiroCurveType(String friendlyName)
        {
            this.friendlyName = friendlyName;
        }

        public String getFriendlyName()
        {
            return friendlyName;
        }
    }

    private static final double DEFAULT_GLOBAL_ALPHA = 1;
    private static final int DEFAULT_SHADOW_BLUR = 2;

    private static final double SPIRO_CURVE_WIDTH = 30;
    private static final double SPIRO_CURVE_SPEED_Y = 0.4;
    private static final int VELOCITY_SMOOTHING = 10;
    private static final int POSITION_SMOOTHING = 2;
    private static final double DEFAULT_SPLINE_TENSION = 0.4;
    private static final double DEFAULT_SPIRO_RESOLUTION = 1;

    private CanvasToolEvents _toolEvents = new CanvasToolEvents(this);
    private SketchData data = null;

    private final RegistrationsManager registrationsManager = new RegistrationsManager();
    private final RegistrationsManager untilMovementStopRegs = new RegistrationsManager();
    private final SketchToolbar _toolbar = new SketchToolbar();

    private final PointUtils.MovingAverage _averageVelocity = new PointUtils.MovingAverage(VELOCITY_SMOOTHING);
    private final PointUtils.MovingAverage _averageDrawPos = new PointUtils.MovingAverage(POSITION_SMOOTHING);

    private final Canvas _canvas = Canvas.createIfSupported();
    private final Canvas _cursorCanvas = Canvas.createIfSupported();
    private Canvas _resizeCanvas1 = Canvas.createIfSupported();
    private Canvas _resizeCanvas2 = Canvas.createIfSupported();

    // private Canvas _undoCanvas = Canvas.createIfSupported();
    private boolean _inViewMode = false;
    private boolean _active = false;
    private boolean _bound = false;

    private boolean _drawingPathExists;

    private Point2D _prevMousePos = Point2D.zero;

    private Point2D _prevDrawPos1 = Point2D.zero;
    private Point2D _prevDrawPos2 = Point2D.zero;
    private Point2D _prevControlPoint = Point2D.zero;

    private Context2d _context = null;
    private final ImageElement _imageElement;

    private Image _image = new Image();

    private double _spiroCurveParameter = 0;

    //TODO: Use Widget.isAttached instead (which currently doesn't seem to work - always returns false)
    private boolean _isAttached;

    public SketchTool(int width, int height)
    {
        this._imageElement = ImageElement.as(this._image.getElement());
        this.add(this._image);

        if (Canvas.isSupported()) {
            this.add(this._cursorCanvas);
            this._context = this._canvas.getContext2d();

            // // DEBUGGING
            // this.add(this._resizeCanvas1);
            // this._resizeCanvas1.getElement().getStyle().setPosition(Position.ABSOLUTE);
            // this._resizeCanvas1.getElement().getStyle().setLeft(100, Unit.PCT);
            // this._resizeCanvas1.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
            // this.add(this._resizeCanvas2);
            // this._resizeCanvas2.getElement().getStyle().setPosition(Position.ABSOLUTE);
            // this._resizeCanvas2.getElement().getStyle().setTop(100, Unit.PCT);
            // this._resizeCanvas1.getElement().getStyle().setBorderStyle(BorderStyle.DASHED);
            // // ---------
        }

        this.updateImageVisibilty();
        this.setWidth(width);
        this.setHeight(height);
        this.addStyleName(CanvasResources.INSTANCE.main().sketchTool());
    }

    @Override
    public void bind()
    {
        this._bound = true;
        this.updateViewMode();
    }

    @Override
    public boolean canRotate()
    {
        // TODO: disabled because we don't know how to translate mouse coordinates when the tool is rotated. It needs to
        // be done relative to the tool frame, because that is the element that is rotated (not the tool itself).
        return true;
    }

    @Override
    public ResizeMode getResizeMode()
    {
        return ResizeMode.BOTH;
    }

    @Override
    public IsWidget getToolbar()
    {
        return this._toolbar;
    }

    @Override
    public ICanvasToolEvents getToolEvents()
    {
        return this._toolEvents;
    }

    @Override
    public SketchData getValue()
    {
        if (null == this._context) {
            // we can't change anything.
            return this.data;
        }
        this.updateDataFromCanvas();
        return this.data;
    }

    @Override
    public void onResize()
    {
        Point2D targetSize = ElementUtils.getElementOffsetSize(this.getElement());

        // make sure resizeCanvas1 is big enough for the new canvas size
        this.expandBackCanvas(targetSize);

        // TODO: it should have been enough to draw with Composite.COPY, and no need to clear-before-draw
        // but that seems to screw up the transparency.
        CanvasUtils.drawOnto(this._canvas, this._resizeCanvas1, Composite.SOURCE_OVER, true, true);

        // this will clear _canvas
        this.setWidth(targetSize.getX());
        this.setHeight(targetSize.getY());

        CanvasUtils.drawOnto(this._resizeCanvas1, this._canvas, Composite.SOURCE_OVER);

        this.redraw();
    }

    @Override
    public void setActive(boolean isActive)
    {
        this._active = isActive;
        if (this._inViewMode) {
            return;
        }
    }

    @Override
    public void setElementData(ElementData data)
    {
        this.setValue((SketchData) data);
    }

    @Override
    public void setValue(SketchData value)
    {
        this.data = value;
        this._toolbar.setOptions(this.data.sketchOptions);
        this.refreshCanvasFromData();
    }

    @Override
    public void setViewMode(boolean isViewMode)
    {
        this._inViewMode = isViewMode;
        this.updateViewMode();
    }


    @Override
    protected void onAttach() {
        super.onAttach();
        this._isAttached = true;
    }

    @Override
    protected void onDetach() {
        this._isAttached = false;
        super.onDetach();
    }

    @Override
    protected void onLoad()
    {
        super.onLoad();
        ElementUtils.setTextSelectionEnabled(this.getElement(), false);
        this.refreshCanvasFromData();
        this.updateViewMode();
    }

    @Override
    protected void onUnload()
    {
        this.updateDataFromCanvas();
        this.registrationsManager.clear();
        UndoManager.get().removeOwner(this);
        super.onUnload();
    }

    /**
     * Does not check the buttons' states, instead it relies on drawingPathExists.
     * Because we can't use getButton or event.getNativeButton from within a MouseMove event handler.
     * @see http://code.google.com/p/google-web-toolkit/issues/detail?id=3983
     */
    private void handleMovementEvent()
    {
        if (false == isDrawingActive()) {
            return;
        }
        Point2D pos = ElementUtils.getMousePositionRelativeToElement(this.getElement());
        if (DrawingTool.PAINT != this.data.sketchOptions.drawingTool) {
            this.drawLinearInterpolatedSteps(pos);
        }
        this.applyDrawingTool(pos, pos.minus(PointUtils.nullToZero(this._prevMousePos)));
        this._prevMousePos = pos;
    }

    private void applyDrawingTool(final Point2D mousePos, final Point2D velocity)
    {
        SketchOptions sketchOptions = this.data.sketchOptions;

        this._context.setStrokeStyle(sketchOptions.penColor);
        this._context.setShadowColor(sketchOptions.penColor);
        this._context.setLineWidth(sketchOptions.penWidth);

        if (isErasing()) {
            // We have to erase in both buffers, because when we copy from the front to the back buffer later when
            // resizing, it does not
            // overwrite with transparent pixels
            this.drawEraser(mousePos, this._context);
            this.drawEraser(mousePos, this._resizeCanvas1.getContext2d());
            return;
        }

        this.applyStrokeDrawingTool(mousePos, velocity, sketchOptions);
    }

    private void applyStrokeDrawingTool(final Point2D mousePos, Point2D velocity, SketchOptions sketchOptions)
    {
        Point2D finalPos = mousePos;
        this._averageVelocity.add(velocity);
        Point2D averageVelocity = this._averageVelocity.getAverage();
        if (DrawingTool.SPIRO == sketchOptions.drawingTool) {
            if (averageVelocity.getAbs().sumCoords() < 1) {
                return;
            }
            finalPos = this.getSpiroPoint(mousePos, averageVelocity, this.getCurvePointForSpiro(1));
            // finalPos = this._averageDrawPos.getAverage();
        }
        this._averageDrawPos.add(finalPos);
        finalPos = this._averageDrawPos.getAverage();

        if (sketchOptions.useBezierSmoothing) {
            this.drawBezierLine(finalPos);
        } else {
            if (null == this._prevDrawPos1) {
                this._context.moveTo(finalPos.getX(), finalPos.getY());
            }
            this._context.lineTo(finalPos.getX(), finalPos.getY());
            this._context.stroke();
            if (DrawingTool.SPIRO == sketchOptions.drawingTool) {
                this._context.beginPath();
            }
            this._context.moveTo(finalPos.getX(), finalPos.getY());
        }

        this._prevDrawPos2 = this._prevDrawPos1;
        this._prevDrawPos1 = finalPos;
    }

    private void drawBezierLine(Point2D finalPos)
    {
        if ((null == this._prevDrawPos1) || (null == this._prevDrawPos2)) {
            return;
        }
        // -----------
        Pair<Point2D, Point2D> controlPoints = PointUtils.getBezierControlPoints(this._prevDrawPos2, this._prevDrawPos1, finalPos, DEFAULT_SPLINE_TENSION);
        Point2D cp0 = this._prevControlPoint;
        Point2D cp1 = controlPoints.getA();
        Point2D cp2 = controlPoints.getB();
        if (null == cp0) {
            cp0 = cp1;
        }
        this._prevControlPoint = cp2;

        this._context.beginPath();
        this._context.moveTo(this._prevDrawPos2.getX(), this._prevDrawPos2.getY());
        this._context.bezierCurveTo(cp0.getX(), cp0.getY(), cp1.getX(), cp1.getY(), this._prevDrawPos1.getX(), this._prevDrawPos1.getY());
        this._context.stroke();
    }

    private void drawCursor()
    {
        Point2D pos = ElementUtils.getMousePositionRelativeToElement(this.getElement());
        final Rectangle sizeRectangle = new Rectangle(Point2D.zero,
                ElementUtils.getElementOffsetSize(this.getElement()));
        if ((null == pos) || (false == sizeRectangle.contains(pos))) {
            return;
        }
        Context2d cursorContext = this._cursorCanvas.getContext2d();
        if (this.isErasing()) {
            String eraserStrokeColor = "black";
            if (this.isDrawingActive()) {
                eraserStrokeColor = "red";
            }
            cursorContext.setStrokeStyle(eraserStrokeColor);
            cursorContext.setFillStyle("rgba(255,255,255,0.5)");
            cursorContext.setLineWidth(1);
            cursorContext.beginPath();
            cursorContext.rect(pos.getX() - this.data.sketchOptions.eraserWidth / 2,
                               pos.getY() - this.data.sketchOptions.eraserWidth / 2,
                               this.data.sketchOptions.eraserWidth,
                               this.data.sketchOptions.eraserWidth);
            cursorContext.closePath();
            if (this.isDrawingActive()) {
                cursorContext.fill();
            }
            cursorContext.stroke();
        } else {
            cursorContext.setStrokeStyle("transparent");
            cursorContext.setFillStyle(this.data.sketchOptions.penColor);
            cursorContext.beginPath();
            cursorContext.arc(pos.getX(), pos.getY(), this.data.sketchOptions.penWidth / 2, 0, Math.PI * 2);
            cursorContext.closePath();
            cursorContext.fill();
        }
    }

    private void drawEraser(Point2D mousePos, Context2d context)
    {
        context.clearRect(mousePos.getX() - this.data.sketchOptions.eraserWidth / 2,
                          mousePos.getY() - this.data.sketchOptions.eraserWidth / 2,
                          this.data.sketchOptions.eraserWidth,
                          this.data.sketchOptions.eraserWidth);
    }

    private boolean isDrawingActive()
    {
        return this._active && this._drawingPathExists;// null != this._currentPath;
    }

    private void drawLinearInterpolatedSteps(Point2D pos)
    {
        if (null == this._prevMousePos) {
            return;
        }
        final Point2D offset = pos.minus(this._prevMousePos);
        Point2D prevStepPos = this._prevMousePos;
        int steps = (int) Math.floor(offset.getRadius() * DEFAULT_SPIRO_RESOLUTION);
        for (int i = 0; i < steps; i += Math.max(1, this.data.sketchOptions.penSkip)) {
            Point2D stepPos = this._prevMousePos.plus(offset.mul(((double) i) / steps));
            this.applyDrawingTool(stepPos, stepPos.minus(prevStepPos));
            prevStepPos = stepPos;
        }
        this._prevMousePos = prevStepPos;
    }

    /**
     * Increases the size of the resize canvases to be at least as big as the given target size, while retaining the
     * bitmap data they are storing.
     */
    private void expandBackCanvas(Point2D targetSize)
    {
        Point2D maxSize = Point2D.max(CanvasUtils.getCoorinateSpaceSize(this._resizeCanvas1), targetSize);
        CanvasUtils.setCoordinateSpaceSize(this._resizeCanvas2, maxSize);

        CanvasUtils.drawOnto(this._resizeCanvas1, this._resizeCanvas2, Composite.COPY);
        this.swapResizeCanvases();
    }

    private Point2D getCurvePointForSpiro(double step)
    {
        this._spiroCurveParameter += step;
        switch (this.data.sketchOptions.spiroCurveType) {
        case Sine:
            return new Point2D(0, // (int)(this._spiroCurveParameter * SPIRO_CURVE_SPEED_X),
                    (int) Math.round(SPIRO_CURVE_WIDTH * Math.cos(this._spiroCurveParameter * SPIRO_CURVE_SPEED_Y)));
        case Circle:
            return new Point2D((int) Math.round(SPIRO_CURVE_WIDTH * Math.sin(this._spiroCurveParameter * SPIRO_CURVE_SPEED_Y)),
                               (int) Math.round(SPIRO_CURVE_WIDTH * Math.cos(this._spiroCurveParameter * SPIRO_CURVE_SPEED_Y)));
        default:
            return Point2D.zero;
        }

    }

    /**
     * Calculates the point of a "spirograph" - overlay of a curve onto the path of another, which can be seen as
     * translating each point of the curve to the coordinate system of the tangent to the target path.
     *
     * In mathematical terms we are changing the basis of the curve to be the given normalized tangent. It's a matrix
     * multiplication.
     *
     * Let <el><li>(f'x, f'y) be the <em>normalized</em> derivative of the target path f</li> <li>(gx, gy) be the curve
     * to be overlayed</li></el> Then the result (rx, ry) is:
     *
     * <pre>
     * (rx) equals (f'x  -f'y)  (gx)
     * (ry)        (f'y   f'x)  (gy)
     * </pre>
     *
     * @param normalizedPathDerivative
     *            derivative of the target path at the desired point
     * @param overlayedCurvePoint
     *            the vector of the overlayed curve at the desired point
     */
    private Point2D getSpiroPoint(Point2D pathPoint, Point2D pathDerivative, Point2D overlayedCurvePoint)
    {
        double magnitude = pathDerivative.getRadius();
        int x = (int) Math.round((overlayedCurvePoint.getX() * pathDerivative.getX() - overlayedCurvePoint.getY()
                * pathDerivative.getY())
                / magnitude);
        int y = (int) Math.round((overlayedCurvePoint.getX() * pathDerivative.getY() + overlayedCurvePoint.getY()
                * pathDerivative.getX())
                / magnitude);
        return new Point2D(x, y).plus(pathPoint);
    }

    private boolean isErasing()
    {
        return DrawingTool.ERASE == this.data.sketchOptions.drawingTool;
    }

    private void redraw()
    {
        this.redraw(true);
    }

    private void redraw(boolean drawCursor)
    {
        CanvasUtils.setCoordinateSpaceSize(this._cursorCanvas, CanvasUtils.getCoorinateSpaceSize(this._canvas));
        if (drawCursor) {
            this.drawCursor();
        }
        CanvasUtils.drawOnto(this._canvas, this._cursorCanvas, Composite.DESTINATION_OVER);
    }

    private void refreshCanvasFromData()
    {
        this._imageElement.setSrc(Strings.nullToEmpty(this.data.imageData));
        if (null == this._context) {
            return;
        }
        // For some reason, the canvas does not get updated after a page reload if not using deferred command in IE (at
        // least, maybe also others)
        if (null != data.transform.size) {
            CanvasUtils.setCoordinateSpaceSize(_canvas, data.transform.size);
        }
        if (this._isAttached){
            _context.drawImage(_imageElement, 0, 0);
            redraw(false);
        }
    }

    private void setContextConstantProperties()
    {
        this._context.setGlobalAlpha(DEFAULT_GLOBAL_ALPHA);
        this._context.setShadowBlur(DEFAULT_SHADOW_BLUR);
        this._context.setFillStyle("transparent");
        this._context.setLineJoin(LineJoin.ROUND);
        this._context.setLineCap(LineCap.ROUND);
    }

    private void setHeight(int height)
    {
        super.setHeight(toPxString(height));
        this._canvas.setCoordinateSpaceHeight(height);
    }

    private void setOptions(SketchOptions options)
    {
        this.data.sketchOptions = options;
//        switch (this.data.sketchOptions.drawingTool)
//        {
//        case ERASE:
//            this._averageDrawPos.setNumBins(POSITION_SMOOTHING_ERASE)
//        case SPIRO:
//        case PAINT:
//        default:
//
//        }
    }

    private void setRegistrations()
    {
        final SketchTool that = this;
        this.registrationsManager.clear();

        this.registrationsManager.add(this._toolbar.addOptionsChangedHandler(new Handler<SketchOptions>() {
            @Override public void onFire(SketchOptions arg) {
                that.setOptions(arg);
            }
        }));

        if (false == Canvas.isSupported()) {
            return;
        }

        this.registrationsManager.add(this.addDomHandler(new MouseOutHandler() {
            @Override public void onMouseOut(MouseOutEvent event) {
                that.handleMovementEvent();
                that.redraw(false); // cursor left the canvas area, need to redraw without it
            }
        }, MouseOutEvent.getType()));
        this.registrationsManager.add(this.addDomHandler(new MouseOverHandler() {
            @Override public void onMouseOver(MouseOverEvent event) {
                if (that.isDrawingActive()) {
                    // restart the path to prevent drawing line from mouse-out pos to mouse-over pos
                    that.startPathDraw();
                }
            }}, MouseOverEvent.getType()));
        this.registrationsManager.add(WidgetUtils.addMovementStartHandler(this, new Handler<HumanInputEvent<?>>() {
            @Override public void onFire(HumanInputEvent<?> arg) {
                that.untilMovementStopRegs.clear();
                that.untilMovementStopRegs.add(Event.addNativePreviewHandler(new NativePreviewHandler(){
                    @Override public void onPreviewNativeEvent(NativePreviewEvent event) {
                        if (EventUtils.nativePreviewEventTypeIsAny(event, MouseUpEvent.getType(), TouchEndEvent.getType())) {
                            that.untilMovementStopRegs.clear();
                            terminateDrawingPath();
                            redraw(false);
                        }
                    }}));
                // TODO request to be activated instead of doing this forcefully?
                // we want the tool frame to know it is activated.
                that.setActive(true);
                that.startPathDraw();
            }
        }));
        this.registrationsManager.add(WidgetUtils.addMovementStopHandler(this, new Handler<HumanInputEvent<?>>() {
            @Override public void onFire(HumanInputEvent<?> arg) {
                that.untilMovementStopRegs.clear();
                if (that.isDrawingActive()) {
                    that.terminateDrawingPath();
                }
            }
        }));

        this.registrationsManager.add(WidgetUtils.addMovementMoveHandler(this, new Handler<HumanInputEvent<?>>() {
            @Override public void onFire(HumanInputEvent<?> arg) {
                //that.untilMouseOverRegs.clear();
                that.handleMovementEvent();
                that.redraw(); // to update both the drawn graphics and the cursor
            }
        }));
    }

    private void setWidth(int width)
    {
        super.setWidth(toPxString(width));
        this._canvas.setCoordinateSpaceWidth(width);
    }

    private void startPathDraw()
    {
        // this.saveToUndoCanvas();

        Point2D pos = ElementUtils.getMousePositionRelativeToElement(this.getElement());
        this._drawingPathExists = true;
        this._context.beginPath();
        this._averageDrawPos.clear();
        this._averageVelocity.clear();
        this._prevMousePos = null;
        this._prevDrawPos1 = null;
        this._prevDrawPos2 = null;
        this._prevControlPoint = null;

        this.setContextConstantProperties();

        this.applyDrawingTool(pos, Point2D.zero);
    }

    private void swapResizeCanvases()
    {
        Canvas tempCanvas = this._resizeCanvas2;
        this._resizeCanvas2 = this._resizeCanvas1;
        this._resizeCanvas1 = tempCanvas;
    }

    private void terminateDrawingPath()
    {
        this._drawingPathExists = false;
        // TODO: handle undo
        // // We can only handle one undo step.
        // UndoManager.get().removeOwner(this);
        // UndoManager.get().add(this, new UndoRedoPair() {
        // @Override
        // public void undo()
        // {
        //
        // }
        //
        // @Override
        // public void redo()
        // {
        // }
        // });
    }

    // private void saveToUndoCanvas()
    // {
    // CanvasUtils.setCoordinateSpaceSize(this._undoCanvas, CanvasUtils.getCoorinateSpaceSize(this._canvas));
    // CanvasUtils.drawOnto(this._canvas, this._undoCanvas);
    // }
    //
    // private void restoreFromUndoCanvas()
    // {
    // CanvasUtils.drawOnto(this._undoCanvas, this._canvas);
    // }
    //

    private String toPxString(int height)
    {
        return String.valueOf(height) + "px";
    }

    private void updateDataFromCanvas()
    {
        if (null != this._canvas) {

            // TODO: for some reasno adding a condition about isAttached always returns false here
            //&& (this._canvas.isAttached())) {
            // Browser bug?

            final String dataUrl = Strings.nullToEmpty(this._canvas.toDataUrl());
            this.data.imageData = dataUrl;
            this._image.setUrl(dataUrl);
        }
    }

    private void updateImageVisibilty()
    {
        final boolean imageVisible = this._inViewMode || (null == _canvas);
        if (imageVisible) {
            this.updateDataFromCanvas();
        }
        this._image.setVisible(imageVisible);
        this._cursorCanvas.setVisible((false == this._inViewMode) && (null != _canvas));
    }

    private void updateViewMode()
    {
        if (this._inViewMode) {
            this.registrationsManager.clear();
        } else if (this._bound) {
            this.setRegistrations();
            this.redraw();
        }
        this.updateImageVisibilty();
    }
}
