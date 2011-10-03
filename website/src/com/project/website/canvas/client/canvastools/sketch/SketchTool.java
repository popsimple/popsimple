package com.project.website.canvas.client.canvastools.sketch;

import java.util.ArrayList;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.Positionable;
import org.vaadin.gwtgraphics.client.VectorObject;
import org.vaadin.gwtgraphics.client.shape.Circle;
import org.vaadin.gwtgraphics.client.shape.Path;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.dom.client.HumanInputEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.client.handlers.RegistrationsManager;
import com.project.shared.client.utils.ElementUtils;
import com.project.shared.client.utils.NodeUtils;
import com.project.shared.client.utils.widgets.WidgetUtils;
import com.project.shared.data.Point2D;
import com.project.website.canvas.client.canvastools.base.CanvasTool;
import com.project.website.canvas.client.canvastools.base.CanvasToolEvents;
import com.project.website.canvas.client.canvastools.base.ICanvasToolEvents;
import com.project.website.canvas.client.canvastools.base.ResizeMode;
import com.project.website.canvas.client.resources.CanvasResources;
import com.project.website.canvas.client.shared.UndoManager;
import com.project.website.canvas.shared.data.ElementData;
import com.project.website.canvas.shared.data.VectorGraphicsData;

public class SketchTool extends DrawingArea implements CanvasTool<VectorGraphicsData>
{
    private CanvasToolEvents _toolEvents = new CanvasToolEvents(this);

    private VectorGraphicsData data = null;
    private final RegistrationsManager registrationsManager = new RegistrationsManager();

    private final SketchToolbar _toolbar = new SketchToolbar();

    protected Path _currentPath = null;
    protected boolean _inViewMode = false;
    protected boolean _active = false;
    protected boolean _bound = false;

    protected String _strokeColor = "#000000";

    private boolean _drawingPathExists;

    private Point2D _prevDrawPos = Point2D.zero;

    private Circle _cursor;

    public SketchTool(int width, int height) {
        super(width, height);
        this.addStyleName(CanvasResources.INSTANCE.main().sketchTool());
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
    public void setValue(VectorGraphicsData value) {
        this.data = value;
        ElementUtils.setTextSelectionEnabled(this.getElement(), false);
        if (this.getElement().getInnerHTML().equals(this.data.svgString)) {
            return;
        }
        this.clear();
        Element svgElement = this.getElement().getElementsByTagName("svg").getItem(0);
        DivElement tempElement = Document.get().createDivElement();
        tempElement.setInnerHTML(this.data.svgString);
        for (Node node : NodeUtils.fromNodeList(tempElement.getChildNodes())) {
            svgElement.appendChild(node);
        }
    }

    @Override
    public VectorGraphicsData getValue() {
        this.data.svgString = this.getElement().getInnerHTML();
        return this.data;
    }

    @Override
    public void setElementData(ElementData data) {
        this.setValue((VectorGraphicsData) data);
    }

    @Override
    public void setActive(boolean isActive) {
        this._active = isActive;
        if (this._inViewMode) {
            return;
        }
        if (false == isActive) {
            this._currentPath = null;
            this.removeCursor();
        }
    }

    private void removeCursor()
    {
        if (null == this._cursor) {
            return;
        }

        this.remove(this._cursor);
        this._cursor = null;
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
        return false;
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
            this.removeCursor();
        }
        else if (this._bound) {
            this.setRegistrations();
        }
    }

    @Override
    public void onResize() {
        this.setWidth(this.getOffsetWidth());
        this.setHeight(this.getOffsetHeight());
    }

    @Override
    public IsWidget getToolbar() {
        return this._toolbar;
    }

    private void setRegistrations()
    {
        final SketchTool that = this;
        this.registrationsManager.clear();

        this.registrationsManager.add(this.addMouseOutHandler(new MouseOutHandler() {
            @Override public void onMouseOut(MouseOutEvent event) {
                that.removeCursor();
                that._prevDrawPos = null;
            }
        }));
        this.registrationsManager.add(WidgetUtils.addMovementStartHandler(this, new Handler<HumanInputEvent<?>>() {
            @Override public void onFire(HumanInputEvent<?> arg) {
                // TODO request to be activated instead of doing this forcefully?
                // we want the tool frame to know it is activated.
                that.setActive(true);
                that.startPathDraw();
            }}));
        this.registrationsManager.add(WidgetUtils.addMovementStopHandler(this, new Handler<HumanInputEvent<?>>() {
            @Override public void onFire(HumanInputEvent<?> arg) {
                //final Path path = that._currentPath;
                if ((false == that._active) || (false == that.drawingPathExists())) {
                    return;
                }
//                UndoManager.get().add(this, new UndoRedoPair() {
//                    @Override public void undo() {
//                        that.remove(path);
//                    }
//
//                    @Override public void redo() {
//                        that.add(path);
//                    }
//                });
                terminateDrawingPath();
            }}));

        this.registrationsManager.add(this.addDomHandler(new MouseMoveHandler(){
            @Override public void onMouseMove(MouseMoveEvent event) {
                that.updateCursor();
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
    }

    private void startPathDraw()
    {
        Point2D pos = ElementUtils.getMousePositionRelativeToElement(this.getElement());
//        this._currentPath = new Path(pos.getX(), pos.getY());
//        this._currentPath.setStrokeColor("#000000");
//        this._currentPath.setFillOpacity(0);
//        this._currentPath.setStrokeWidth(this.data.penWidth);
//        this._currentPath.setStrokeColor(this._strokeColor);
//        this.add(this._currentPath);
        this._drawingPathExists = true;
        this.drawPen(pos);
        this._prevDrawPos = pos;
    }

    private Circle createDrawingCircle(Point2D pos)
    {
        Circle circle = new Circle(pos.getX(), pos.getY(), this.data.penWidth);
        circle.setFillColor(this._strokeColor);
        circle.setStrokeWidth(0);
        return circle;
    }

    private void addLineToPath()
    {
        if (drawingPathExists()) {
            Point2D pos = ElementUtils.getMousePositionRelativeToElement(this.getElement());
            //this._currentPath.lineTo(pos.getX(), pos.getY());
            if ((false == this._toolbar.isErasing()) && (null != this._prevDrawPos)) {
                final Point2D offset = pos.minus(this._prevDrawPos);
                int steps = (int) Math.floor(offset.radius());
                for (int i = 0 ; i < steps; i += Math.max(1, this.data.penSkip)) {
                    Point2D stepPos = this._prevDrawPos.plus(offset.mul(((double)i)/steps));
                    this.drawPen(stepPos);
                }
            }
            this._prevDrawPos = pos;
            this.drawPen(pos);
        }
    }

    private void drawPen(Point2D stepPos)
    {
        if (this._toolbar.isErasing()) {
            ArrayList<VectorObject> objsToRemove = new ArrayList<VectorObject>();
            for (int i = 0 ; i < this.getVectorObjectCount(); i++) {
                VectorObject obj = this.getVectorObject(i);
                if (obj == this._cursor) {
                    continue;
                }
                if (obj instanceof Positionable) {
                    Positionable positionableObj = (Positionable) obj;
                    Point2D objPos = new Point2D(positionableObj.getX(), positionableObj.getY());
                    if (objPos.minus(stepPos).radius() < this.data.eraserWidth + this.data.penWidth) {
                        objsToRemove.add(obj);
                    }
                }
            }
            for (VectorObject obj : objsToRemove) {
                this.remove(obj);
            }
        }
        else {
            this.add(this.createDrawingCircle(stepPos));
        }
    }

    private boolean drawingPathExists()
    {
        return this._drawingPathExists;//null != this._currentPath;
    }


    protected void setColor(String arg)
    {
        this._strokeColor = arg;
        if (drawingPathExists()) {
            //this._currentPath.setStrokeColor(arg);
        }
    }

    private void terminateDrawingPath()
    {
        _currentPath = null;
        _drawingPathExists = false;
    }

    private void updateCursor()
    {
        if (null == this._cursor) {
            this._cursor = this.createDrawingCircle(Point2D.zero);
            this.add(this._cursor);
        }
        Point2D mousePos = ElementUtils.getMousePositionRelativeToElement(this.getElement());
        this._cursor.setX(mousePos.getX());
        this._cursor.setY(mousePos.getY());
        if (this._toolbar.isErasing()) {
            this._cursor.setFillColor("transparent");
            this._cursor.setStrokeColor("black");
            this._cursor.setStrokeWidth(1);
            this._cursor.setRadius(this.data.eraserWidth);
        }
        else {
            this._cursor.setStrokeWidth(0);
            this._cursor.setFillColor(this._toolbar.getColor());
            this._cursor.setRadius(this.data.penWidth);
        }
    }

    @Override
    public boolean dimOnLoad() {
        return true;
    }
}

