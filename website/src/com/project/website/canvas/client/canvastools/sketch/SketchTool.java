package com.project.website.canvas.client.canvastools.sketch;

import com.google.common.base.Strings;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.LineCap;
import com.google.gwt.canvas.dom.client.Context2d.LineJoin;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.HumanInputEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.client.handlers.RegistrationsManager;
import com.project.shared.client.utils.CanvasUtils;
import com.project.shared.client.utils.ElementUtils;
import com.project.shared.client.utils.SchedulerUtils.OneTimeScheduler;
import com.project.shared.client.utils.widgets.WidgetUtils;
import com.project.shared.data.Point2D;
import com.project.shared.utils.PointUtils;
import com.project.website.canvas.client.canvastools.base.CanvasTool;
import com.project.website.canvas.client.canvastools.base.CanvasToolEvents;
import com.project.website.canvas.client.canvastools.base.ICanvasToolEvents;
import com.project.website.canvas.client.canvastools.base.ResizeMode;
import com.project.website.canvas.client.resources.CanvasResources;
import com.project.website.canvas.client.shared.UndoManager;
import com.project.website.canvas.shared.data.ElementData;
import com.project.website.canvas.shared.data.SketchData;
import com.project.website.canvas.shared.data.SketchOptions;

public class SketchTool extends FlowPanel implements CanvasTool<SketchData>
{
    public enum SpiroCurveType {
        Sine,
        Circle
    }


    private static final double SPIRO_CURVE_WIDTH = 40;
    private static final double SPIRO_CURVE_SPEED_Y = 0.4;
	private static final int VELOCITY_SMOOTHING = 15;
	private static final int POSITION_SMOOTHING = 3;

    // TODO: for IE <= 8, call this instead of Canvas.getContext2d
    private static final native Context2d getContext2d(Element canvasElement) /*-{
        $wnd.G_vmlCanvasManager.initElement(el);
        return el.getContext('2d');
    }-*/;

    private CanvasToolEvents _toolEvents = new CanvasToolEvents(this);
    private SketchData data = null;

    private final RegistrationsManager registrationsManager = new RegistrationsManager();
    private final SketchToolbar _toolbar = new SketchToolbar();
    
    private final PointUtils.MovingAverage _averageVelocity = new PointUtils.MovingAverage(VELOCITY_SMOOTHING);
    private final PointUtils.MovingAverage _averageDrawPos = new PointUtils.MovingAverage(POSITION_SMOOTHING);

    private final Canvas _canvas = Canvas.createIfSupported();
    private final Canvas _cursorCanvas = Canvas.createIfSupported();
    private Canvas _resizeCanvas1 = Canvas.createIfSupported();
    private Canvas _resizeCanvas2 = Canvas.createIfSupported();
    
//    private Canvas _undoCanvas = Canvas.createIfSupported();
    private boolean _inViewMode = false;
    private boolean _active = false;
    private boolean _bound = false;
    
    
    private String _strokeColor = "#000000";

    private SpiroCurveType _curveType = SpiroCurveType.Circle;

    private boolean _drawingPathExists;

    private Point2D _prevDrawPos = Point2D.zero;
    private Context2d _context = null;
    private final ImageElement _imageElement;

    private Image _image = new Image();

    private DrawingTool _activeDrawingTool;

    private double _spiroCurveParameter = 0;

    private final ScheduledCommand drawFromImageCommand = new ScheduledCommand() {
        @Override public void execute() {
            CanvasUtils.setCoordinateSpaceSize(_canvas, data.transform.size);
            _context.drawImage(_imageElement, 0, 0);
        }
    };

    private final ScheduledCommand redrawCommand = new ScheduledCommand() {
        @Override public void execute() {
            redraw();
        }
    };


    public SketchTool(int width, int height) {
        this._imageElement = ImageElement.as(this._image.getElement());
        this.add(this._image);
        this.updateImageVisibilty();

        if (Canvas.isSupported()) {
            this.add(this._cursorCanvas);
            this._context = this._canvas.getContext2d();
        }
        this.setWidth(width);
        this.setHeight(height);
        this.addStyleName(CanvasResources.INSTANCE.main().sketchTool());
    }


    @Override
    public void bind() {
        this._bound = true;
        this.updateViewMode();
    }

    @Override
    public boolean canRotate() {
        // TODO: disabled because we don't know how to translate mouse coordinates when the tool is rotated. It needs to
        // be done relative to the tool frame, because that is the element that is rotated (not the tool itself).
        return true;
    }
    @Override
    public ResizeMode getResizeMode() {
        return ResizeMode.BOTH;
    }

    @Override
    public IsWidget getToolbar() {
        return this._toolbar;
    }

    @Override
    public ICanvasToolEvents getToolEvents()
    {
        return this._toolEvents;
    }

    @Override
    public SketchData getValue() {
        if (null == this._context) {
            // we can't change anything.
            return this.data;
        }
        this.data.imageData = this._context.getCanvas().toDataUrl("image/png");
        return this.data;
    }

    @Override
    public void onResize()
    {
        Point2D targetSize = ElementUtils.getElementOffsetSize(this.getElement());

        this.expandBackCanvas(targetSize);
        CanvasUtils.drawOnto(this._canvas, this._resizeCanvas1);

        this.setWidth(targetSize.getX());
        this.setHeight(targetSize.getY());

        CanvasUtils.drawOnto(this._resizeCanvas1, this._canvas);
    }


    @Override
    public void setActive(boolean isActive) {
        this._active = isActive;
        if (this._inViewMode) {
            return;
        }
    }

    @Override
    public void setElementData(ElementData data) {
        this.setValue((SketchData) data);
    }

    @Override
    public void setValue(SketchData value) {
        this.data = value;
        this._toolbar.setOptions(this.data.sketchOptions);
        this.refreshCanvasFromData();
    }

    @Override
    public void setViewMode(boolean isViewMode) {
        this._inViewMode = isViewMode;
        this.updateViewMode();
    }


    @Override
    protected void onLoad()
    {
        ElementUtils.setTextSelectionEnabled(this.getElement(), false);
        this.refreshCanvasFromData();
        this.updateViewMode();
    }

    @Override
    protected void onUnload() {
        this.updateImageFromCanvas();
        this.registrationsManager.clear();
        UndoManager.get().removeOwner(this);
        super.onUnload();
    }

    private void addLineToPath()
    {
        if (drawingPathExists()) {
            Point2D pos = ElementUtils.getMousePositionRelativeToElement(this.getElement());
            //this._currentPath.lineTo(pos.getX(), pos.getY());
            if (DrawingTool.PAINT != this.data.sketchOptions.drawingTool) {
                this.drawInterpolatedSteps(pos);
            }
            this.drawPen(pos, pos.minus(PointUtils.nullToZero(this._prevDrawPos)));
            this._prevDrawPos = pos;
            this.redraw();
        }
    }

    private void drawCursor()
    {
        Point2D pos = ElementUtils.getMousePositionRelativeToElement(this.getElement());
        Context2d cursorContext = this._cursorCanvas.getContext2d();
		if (this.isErasing()) {
			cursorContext.setStrokeStyle("black");
            cursorContext.setLineWidth(1);
            cursorContext.beginPath();
            cursorContext.rect(pos.getX() - this.data.sketchOptions.eraserWidth / 2,
                                                   pos.getY() - this.data.sketchOptions.eraserWidth / 2,
                                                   this.data.sketchOptions.eraserWidth, this.data.sketchOptions.eraserWidth);
            cursorContext.stroke();
        }
        else {
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
                          this.data.sketchOptions.eraserWidth, this.data.sketchOptions.eraserWidth);
    }

    private boolean drawingPathExists()
    {
        return this._drawingPathExists;//null != this._currentPath;
    }


    private void drawInterpolatedSteps(Point2D pos)
    {
        if (null != this._prevDrawPos) {
            final Point2D offset = pos.minus(this._prevDrawPos);
            Point2D prevStepPos = this._prevDrawPos;
            int steps = (int) Math.floor(offset.getRadius());
            for (int i = 0 ; i < steps; i += Math.max(1, this.data.sketchOptions.penSkip)) {
                Point2D stepPos = this._prevDrawPos.plus(offset.mul(((double)i)/steps));
                this.drawPen(stepPos, stepPos.minus(prevStepPos));
                prevStepPos = stepPos;
            }
            this._prevDrawPos = prevStepPos;
        }
    }


    private void drawPen(Point2D mousePos, Point2D velocity)
    {
        this._averageVelocity.add(velocity);

        this._context.setStrokeStyle(this.data.sketchOptions.penColor);
        this._context.setFillStyle("transparent");
        this._context.setLineWidth(this.data.sketchOptions.penWidth);
        this._context.setLineJoin(LineJoin.ROUND);
        this._context.setLineCap(LineCap.ROUND);

        if (isErasing()) {
            // We have to erase in both buffers, because when we copy from the front to the back buffer later when resizing, it does not
            // overwrite with transparent pixels
            drawEraser(mousePos, this._context);
            drawEraser(mousePos, this._resizeCanvas1.getContext2d());
            return;
        }
        else {
            Point2D finalPos = mousePos;
            Point2D averageVelocity = this._averageVelocity.getAverage();
            if (DrawingTool.SPIRO == this.data.sketchOptions.drawingTool) {
                if (averageVelocity.getRadius() < 1) {
                    return;
                }
                finalPos = this.getSpiroPoint(mousePos, averageVelocity, this.getCurvePointForSpiro(1));
                //finalPos = this._averageDrawPos.getAverage();
            }
            this._averageDrawPos.add(finalPos);
            finalPos = this._averageDrawPos.getAverage();
            //this._context.arc(finalPos.getX(), finalPos.getY(), this.data.sketchOptions.penWidth, 0, 2 * Math.PI);
            this._context.lineTo(finalPos.getX(), finalPos.getY());
            this._context.moveTo(finalPos.getX(), finalPos.getY());
            this._context.stroke();
//            this._context.closePath();
//            this._context.fill();
        }
    }

    private void expandBackCanvas(Point2D targetSize)
    {
        Point2D maxSize = Point2D.max(CanvasUtils.getCoorinateSpaceSize(this._resizeCanvas1), targetSize);
        CanvasUtils.setCoordinateSpaceSize(this._resizeCanvas2, maxSize);

        CanvasUtils.drawOnto(this._resizeCanvas1, this._resizeCanvas2);
        this.swapResizeCanvases();
    }

    private Point2D getCurvePointForSpiro(double step)
    {
        this._spiroCurveParameter += step;
        switch (this._curveType) {
        case Sine:
            return new Point2D(0, //(int)(this._spiroCurveParameter * SPIRO_CURVE_SPEED_X),
                    (int)Math.round(SPIRO_CURVE_WIDTH * Math.cos(this._spiroCurveParameter * SPIRO_CURVE_SPEED_Y)));
        case Circle:
            return new Point2D((int)Math.round(SPIRO_CURVE_WIDTH * Math.sin(this._spiroCurveParameter * SPIRO_CURVE_SPEED_Y)),
                               (int)Math.round(SPIRO_CURVE_WIDTH * Math.cos(this._spiroCurveParameter * SPIRO_CURVE_SPEED_Y)));
        default:
            return Point2D.zero;
        }

    }

    /**
     * Calculates the point of a "spirograph" - overlay of a curve onto the path of another, which can be seen as
     * translating each point of the curve to the coordinate system of the tangent to the target path.
     *
     * In mathematical terms we are changing the basis of the curve to be the given normalized tangent. It's a matrix multiplication.
     *
     * Let
     * <el><li>(f'x, f'y) be the <em>normalized</em> derivative of the target path f</li>
     *     <li>(gx, gy) be the curve to be overlayed</li></el>
     * Then the result (rx, ry) is:
     * <pre>
     * (rx) equals (f'x  -f'y)  (gx)
     * (ry)        (f'y   f'x)  (gy)
     * </pre>
     * @param normalizedPathDerivative derivative of the target path at the desired point
     * @param overlayedCurvePoint the vector of the overlayed curve at the desired point
     */
    private Point2D getSpiroPoint(Point2D pathPoint, Point2D pathDerivative, Point2D overlayedCurvePoint)
    {
        double magnitude = pathDerivative.getRadius();
        int x = (int)Math.round((overlayedCurvePoint.getX()*pathDerivative.getX() - overlayedCurvePoint.getY()*pathDerivative.getY()) / magnitude);
        int y = (int)Math.round((overlayedCurvePoint.getX()*pathDerivative.getY() + overlayedCurvePoint.getY()*pathDerivative.getX()) / magnitude);
        return new Point2D(x, y).plus(pathPoint);
    }


    private boolean isErasing()
    {
        return DrawingTool.ERASE == this.data.sketchOptions.drawingTool;
    }

    private void redraw()
    {
        CanvasUtils.setCoordinateSpaceSize(this._cursorCanvas, CanvasUtils.getCoorinateSpaceSize(this._canvas));
        CanvasUtils.drawOnto(this._canvas, this._cursorCanvas);
        this.drawCursor();
    }


    private void refreshCanvasFromData()
    {
        final String imageData = this.data.imageData;
        if (null != imageData) {
            this._imageElement.setSrc(imageData);
        }
        else {
            if (null != this._canvas) {
                CanvasUtils.clear(this._canvas);
                CanvasUtils.clear(this._resizeCanvas1);
            }
            return;
        }

        if (null == this._context) {
            return;
        }
        // For some reason, the canvas does not get updated after a page reload if not using deferred command in IE (at
        // least, maybe also others)
        OneTimeScheduler.get().scheduleDeferredOnce(drawFromImageCommand);
    }


    private void setHeight(int height)
    {
        super.setHeight(toPxString(height));
        this._canvas.setCoordinateSpaceHeight(height);
    }


    private void setOptions(SketchOptions options)
    {
        this.data.sketchOptions = options;
    }

    private void setRegistrations()
    {
        final SketchTool that = this;
        this.registrationsManager.clear();

        this.registrationsManager.add(this.addDomHandler(new MouseOutHandler() {
            @Override public void onMouseOut(MouseOutEvent event) {
                that._prevDrawPos = null;
            }
        }, MouseOutEvent.getType()));
        this.registrationsManager.add(WidgetUtils.addMovementStartHandler(this, new Handler<HumanInputEvent<?>>() {
            @Override public void onFire(HumanInputEvent<?> arg) {
                // TODO request to be activated instead of doing this forcefully?
                // we want the tool frame to know it is activated.
                that.setActive(true);
                that.startPathDraw();
            }}));
        this.registrationsManager.add(WidgetUtils.addMovementStopHandler(this, new Handler<HumanInputEvent<?>>() {
            @Override public void onFire(HumanInputEvent<?> arg) {
                if ((false == that._active) || (false == that.drawingPathExists())) {
                    return;
                }
                that.terminateDrawingPath();
            }}));

        this.registrationsManager.add(WidgetUtils.addMovementMoveHandler(this, new Handler<HumanInputEvent<?>>() {
			@Override public void onFire(HumanInputEvent<?> arg) {
                that.redraw();
                if (false == that._active) {
                    return;
                }
                // We can't use getButton or event.getNativeButton
                // from within a MouseMove event handler.
                // see: http://code.google.com/p/google-web-toolkit/issues/detail?id=3983
//                int buttonFlags = event.getNativeButton();
//                if (0 != (buttonFlags & NativeEvent.BUTTON_LEFT)) {
//                    if (null == that._currentPath) {
//                        that.startPathDraw();
//                    }
//                }
                that.addLineToPath();
			}
		}));

        this.registrationsManager.add(this._toolbar.addOptionsChangedHandler(new Handler<SketchOptions>() {
            @Override public void onFire(SketchOptions arg) {
                that.setOptions(arg);
            }}));
    }


    private void setWidth(int width)
    {
        super.setWidth(toPxString(width));
        this._canvas.setCoordinateSpaceWidth(width);
    }


    private void startPathDraw()
    {
//        this.saveToUndoCanvas();

        Point2D pos = ElementUtils.getMousePositionRelativeToElement(this.getElement());
        this._drawingPathExists = true;
        this._context.beginPath();
        this._averageDrawPos.clear();
        this._averageVelocity.clear();
        this.drawPen(pos, Point2D.zero);
        this._prevDrawPos = pos;
    }

    private void swapResizeCanvases()
    {
        Canvas tempCanvas = this._resizeCanvas2;
        this._resizeCanvas2 = this._resizeCanvas1;
        this._resizeCanvas1 = tempCanvas;
    }


    private void terminateDrawingPath()
    {
        _drawingPathExists = false;
        // TODO: handle undo
//        // We can only handle one undo step.
//        UndoManager.get().removeOwner(this);
//        UndoManager.get().add(this, new UndoRedoPair() {
//            @Override
//            public void undo()
//            {
//
//            }
//
//            @Override
//            public void redo()
//            {
//            }
//        });
    }

//    private void saveToUndoCanvas()
//    {
//        CanvasUtils.setCoordinateSpaceSize(this._undoCanvas, CanvasUtils.getCoorinateSpaceSize(this._canvas));
//        CanvasUtils.drawOnto(this._canvas, this._undoCanvas);
//    }
//
//    private void restoreFromUndoCanvas()
//    {
//        CanvasUtils.drawOnto(this._undoCanvas, this._canvas);
//    }
//

    private String toPxString(int height)
    {
        return String.valueOf(height) + "px";
    }

    private void updateImageFromCanvas()
    {
        this._image.setUrl(Strings.nullToEmpty(this._canvas.toDataUrl()));
    }

    private void updateImageVisibilty()
    {
        final boolean imageVisible = this._inViewMode || (null == _canvas);
        if (imageVisible) {
            this.updateImageFromCanvas();
        }
        this._image.setVisible(imageVisible);
        this._cursorCanvas.setVisible((false == this._inViewMode) && (null != _canvas));
    }


    private void updateViewMode()
    {
        if (this._inViewMode) {
            this.registrationsManager.clear();
        }
        else if (this._bound) {
            this.setRegistrations();
        }
        this.updateImageVisibilty();
    }
}


