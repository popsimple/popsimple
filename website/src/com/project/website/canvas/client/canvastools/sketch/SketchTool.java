package com.project.website.canvas.client.canvastools.sketch;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.HumanInputEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.client.handlers.RegistrationsManager;
import com.project.shared.client.utils.CanvasUtils;
import com.project.shared.client.utils.ElementUtils;
import com.project.shared.client.utils.widgets.WidgetUtils;
import com.project.shared.data.Point2D;
import com.project.shared.utils.PointUtils;
import com.project.shared.utils.loggers.Logger;
import com.project.website.canvas.client.canvastools.base.CanvasTool;
import com.project.website.canvas.client.canvastools.base.CanvasToolEvents;
import com.project.website.canvas.client.canvastools.base.ICanvasToolEvents;
import com.project.website.canvas.client.canvastools.base.ResizeMode;
import com.project.website.canvas.client.resources.CanvasResources;
import com.project.website.canvas.client.shared.UndoManager;
import com.project.website.canvas.shared.data.ElementData;
import com.project.website.canvas.shared.data.SketchData;

public class SketchTool extends FlowPanel implements CanvasTool<SketchData>
{
    private static final double SPIRO_CURVE_WIDTH = 30;
    private static final double SPIRO_CURVE_SPEED_X = 1;
    private static final double SPIRO_CURVE_SPEED_Y = 0.1;
    private static final double SPIRO_NORMAL_SCALE = 1000;

    private CanvasToolEvents _toolEvents = new CanvasToolEvents(this);

    private SketchData data = null;
    private final RegistrationsManager registrationsManager = new RegistrationsManager();

    private final SketchToolbar _toolbar = new SketchToolbar();
    private final Canvas _canvas = Canvas.createIfSupported();
    private Canvas _resizeCanvas1 = Canvas.createIfSupported();
    private Canvas _resizeCanvas2 = Canvas.createIfSupported();

    protected boolean _inViewMode = false;
    protected boolean _active = false;
    protected boolean _bound = false;

    protected String _strokeColor = "#000000";

    private boolean _drawingPathExists;

    private Point2D _prevDrawPos = Point2D.zero;

    private Context2d _context = null;
    private final ImageElement _imageElement;
    private Image _image = new Image();

    private DrawingTool _activeDrawingTool;

    private double _spiroCurveParameter = 0;

    public SketchTool(int width, int height) {
        this._imageElement = ImageElement.as(this._image.getElement());
        this.add(this._image);
        this.updateImageVisibilty();

        if (null != _canvas) {
            this.add(this._canvas);
            this._context = this._canvas.getContext2d();
        }
        this.setWidth(width);
        this.setHeight(height);
        this.addStyleName(CanvasResources.INSTANCE.main().sketchTool());
    }


    private void updateImageVisibilty()
    {
        final boolean imageVisible = this._inViewMode || (null == _canvas);
        if (imageVisible) {
            this.updateImageFromCanvas();
        }
        this._image.setVisible(imageVisible);
        this._canvas.setVisible((false == this._inViewMode) && (null != _canvas));
    }


    // TODO: for IE <= 8, call this instead of Canvas.getContext2d
    private static final native Context2d getContext2d(Element canvasElement) /*-{
        $wnd.G_vmlCanvasManager.initElement(el);
        return el.getContext('2d');
    }-*/;

    private void setHeight(int height)
    {
        super.setHeight(toPxString(height));
        this._canvas.setCoordinateSpaceHeight(height);
    }
    private void setWidth(int width)
    {
        super.setWidth(toPxString(width));
        this._canvas.setCoordinateSpaceWidth(width);
    }

    @Override
    public ICanvasToolEvents getToolEvents()
    {
        return this._toolEvents;
    }

    @Override
    protected void onUnload() {
        this.registrationsManager.clear();
        UndoManager.get().removeOwner(this);
        super.onUnload();
    }

    @Override
    protected void onLoad()
    {
        this.updateViewMode();
    }

    @Override
    public void setValue(SketchData value) {
        this.data = value;
        ElementUtils.setTextSelectionEnabled(this.getElement(), false);

        final String imageData = value.imageData;
        if (null != imageData) {
            this._imageElement.setSrc(imageData);
        }
        else {
            if (null != this._canvas) {
                this._canvas.setCoordinateSpaceHeight(this._canvas.getCoordinateSpaceHeight());
            }
            return;
        }

        if (null == this._context) {
            return;
        }

        this._context.drawImage(this._imageElement, 0, 0);
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
    public void setElementData(ElementData data) {
        this.setValue((SketchData) data);
    }

    @Override
    public void setActive(boolean isActive) {
        this._active = isActive;
        if (this._inViewMode) {
            return;
        }
    }


    @Override
    public void bind() {
        this._bound = true;
        this.updateViewMode();
    }

    @Override
    public ResizeMode getResizeMode() {
        return ResizeMode.BOTH;
    }

    @Override
    public boolean canRotate() {
        // TODO: disabled because we don't know how to translate mouse coordinates when the tool is rotated. It needs to
        // be done relative to the tool frame, because that is the element that is rotated (not the tool itself).
        return true;
    }

    @Override
    public void setViewMode(boolean isViewMode) {
        this._inViewMode = isViewMode;
        this.updateViewMode();
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

    @Override
    public void onResize()
    {
        Point2D targetSize = ElementUtils.getElementOffsetSize(this.getElement());

        this.expandBackCanvas(targetSize);
        copyCanvas(this._canvas, this._resizeCanvas1);

        this.setWidth(targetSize.getX());
        this.setHeight(targetSize.getY());

        copyCanvas(this._resizeCanvas1, this._canvas);
    }


    private void expandBackCanvas(Point2D targetSize)
    {
        Point2D maxSize = Point2D.max(CanvasUtils.getCoorinateSpaceSize(this._resizeCanvas1), targetSize);
        CanvasUtils.setCoordinateSpaceSize(this._resizeCanvas2, maxSize);

        copyCanvas(this._resizeCanvas1, this._resizeCanvas2);
        this.swapResizeCanvases();
    }


    private void swapResizeCanvases()
    {
        Canvas tempCanvas = this._resizeCanvas2;
        this._resizeCanvas2 = this._resizeCanvas1;
        this._resizeCanvas1 = tempCanvas;
    }

    private static void copyCanvas(Canvas source, Canvas dest)
    {
        dest.getContext2d().drawImage(source.getCanvasElement(), 0, 0);
    }

    private static void clearCanvas(Canvas canvas)
    {
        canvas.getContext2d().clearRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
    }

    @Override
    public IsWidget getToolbar() {
        return this._toolbar;
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
                terminateDrawingPath();
            }}));

        this.registrationsManager.add(this.addDomHandler(new MouseMoveHandler(){
            @Override public void onMouseMove(MouseMoveEvent event) {
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
            }}, MouseMoveEvent.getType()));

        this.registrationsManager.add(this._toolbar.addColorChangedHandler(new Handler<String>() {
            @Override public void onFire(String arg) {
                that.setColor(arg);
            }}));

        this.registrationsManager.add(this._toolbar.addToolChangedHandler(new Handler<DrawingTool>() {
            @Override public void onFire(DrawingTool arg) {
                that._activeDrawingTool = arg;
            }}));
    }

    private void startPathDraw()
    {
        Point2D pos = ElementUtils.getMousePositionRelativeToElement(this.getElement());
        this._drawingPathExists = true;
        this._context.beginPath();
        this.drawPen(pos, Point2D.zero);
        this._prevDrawPos = pos;
    }


    private void addLineToPath()
    {
        if (drawingPathExists()) {
            Point2D pos = ElementUtils.getMousePositionRelativeToElement(this.getElement());
            //this._currentPath.lineTo(pos.getX(), pos.getY());
            //drawInterpolatedSteps(pos);
            this.drawPen(pos, pos.minus(PointUtils.nullToZero(this._prevDrawPos)));
            this._prevDrawPos = pos;
        }
    }


    private void drawInterpolatedSteps(Point2D pos)
    {
        if (null != this._prevDrawPos) {
            final Point2D offset = pos.minus(this._prevDrawPos);
            Point2D prevStepPos = this._prevDrawPos;
            int steps = (int) Math.floor(offset.getRadius());
            for (int i = 0 ; i < steps; i += Math.max(1, this.data.penSkip)) {
                Point2D stepPos = this._prevDrawPos.plus(offset.mul(((double)i)/steps));
                this.drawPen(stepPos, stepPos.minus(prevStepPos));
                prevStepPos = stepPos;
            }
            this._prevDrawPos = prevStepPos;
        }
    }

    private void drawPen(Point2D mousePos, Point2D velocity)
    {
        //this.add(this.createDrawingCircle(stepPos));
        this._context.setStrokeStyle(this._strokeColor);
        //this._context.setStrokeStyle(CanvasPattern)
        this._context.setFillStyle("transparent");

        if (isErasing()) {
            // We have to erase in both buffers, because when we copy from the front to the back buffer later when resizing, it does not
            // overwrite with transparent pixels
            drawEraser(mousePos, this._context);
            drawEraser(mousePos, this._resizeCanvas1.getContext2d());
            return;
        }
        else {
            Point2D finalPos = mousePos;
            if (DrawingTool.SPIRO == this._activeDrawingTool) {
                if (Objects.equal(Point2D.zero, velocity)) {
                    return;
                }
                finalPos = this.getSpiroPoint(mousePos, velocity, this.getCurvePointForSpiro(velocity.getRadius()));
            }
            //this._context.arc(finalPos.getX(), finalPos.getY(), this.data.penWidth, 0, 2 * Math.PI);
            this._context.lineTo(finalPos.getX(), finalPos.getY());
            this._context.moveTo(finalPos.getX(), finalPos.getY());
            this._context.stroke();
//            this._context.closePath();
//            this._context.fill();
        }
    }


    private boolean isErasing()
    {
        return DrawingTool.ERASE == this._activeDrawingTool;
    }


    private void drawEraser(Point2D mousePos, Context2d context)
    {
        context.clearRect(mousePos.getX() - this.data.eraserWidth / 2,
                                mousePos.getY() - this.data.eraserWidth / 2,
                                this.data.eraserWidth, this.data.eraserWidth);
    }

    private boolean drawingPathExists()
    {
        return this._drawingPathExists;//null != this._currentPath;
    }


    protected void setColor(String arg)
    {
        this._strokeColor = arg;
    }

    private void terminateDrawingPath()
    {
        _drawingPathExists = false;
    }

    private String toPxString(int height)
    {
        return String.valueOf(height) + "px";
    }

    private void updateImageFromCanvas()
    {
        this._image.setUrl(Strings.nullToEmpty(this._canvas.toDataUrl()));
    }

    private Point2D getCurvePointForSpiro(double step)
    {
        this._spiroCurveParameter += step;
        return new Point2D(0, //(int)(this._spiroCurveParameter * SPIRO_CURVE_SPEED_X),
                           (int)Math.round(SPIRO_CURVE_WIDTH * Math.cos(this._spiroCurveParameter * SPIRO_CURVE_SPEED_Y)));
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
        Logger.info(pathDerivative);
        double magnitude = pathDerivative.getRadius();
        int x = (int)Math.round((overlayedCurvePoint.getX()*pathDerivative.getX() - overlayedCurvePoint.getY()*pathDerivative.getY()) / magnitude);
        int y = (int)Math.round((overlayedCurvePoint.getX()*pathDerivative.getY() + overlayedCurvePoint.getY()*pathDerivative.getX()) / magnitude);
        return new Point2D(x, y).plus(pathPoint);
    }
}


