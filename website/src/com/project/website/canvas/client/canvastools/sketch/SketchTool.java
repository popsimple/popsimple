package com.project.website.canvas.client.canvastools.sketch;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.shape.Path;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.client.handlers.RegistrationsManager;
import com.project.shared.client.utils.ElementUtils;
import com.project.shared.client.utils.EventUtils;
import com.project.shared.client.utils.NodeUtils;
import com.project.shared.data.Point2D;
import com.project.website.canvas.client.canvastools.base.CanvasTool;
import com.project.website.canvas.client.shared.UndoManager;
import com.project.website.canvas.client.shared.UndoManager.UndoRedoPair;
import com.project.website.canvas.shared.data.ElementData;
import com.project.website.canvas.shared.data.VectorGraphicsData;

public class SketchTool extends DrawingArea implements CanvasTool<VectorGraphicsData>
{
    private final static int defaultWidth = 200;
    private final static int defaultHeight = 200;

    private VectorGraphicsData data = null;

    public SketchTool() {
        super(defaultWidth, defaultHeight);
    }

    private final RegistrationsManager registrationsManager = new RegistrationsManager();
    protected Path _currentPath = null;
    protected boolean _inViewMode = false;

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
    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler handler) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HandlerRegistration addKillRequestEventHandler(Handler<String> handler) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HandlerRegistration addMoveStartEventHandler(Handler<MouseEvent<?>> handler) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HandlerRegistration addSelfMoveRequestEventHandler(Handler<Point2D> handler) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setElementData(ElementData data) {
        this.setValue((VectorGraphicsData) data);
    }

    @Override
    public void setActive(boolean isActive) {
        if (this._inViewMode) {
            return;
        }
        if (false == isActive) {
            this._currentPath = null;
        }
    }

    @Override
    public void bind() {
        // TODO Auto-generated method stub

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
        }
        else {
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
        // TODO Auto-generated method stub
        return null;
    }

    private void setRegistrations()
    {
        final SketchTool that = this;
        this.registrationsManager.clear();
        this.registrationsManager.add(this.addDomHandler(new MouseDownHandler(){
            @Override public void onMouseDown(MouseDownEvent event) {
                Point2D pos = getMousePositionRelativeToElement(that.getElement());
                that._currentPath = new Path(pos.getX(), pos.getY());
                that._currentPath.setStrokeColor("#000000");
                that._currentPath.setFillOpacity(0);
                that._currentPath.setStrokeWidth(that.data.penWidth);
                that.add(that._currentPath);
            }}, MouseDownEvent.getType()));

        this.registrationsManager.add(this.addDomHandler(new MouseUpHandler(){
            @Override public void onMouseUp(MouseUpEvent event) {
                final Path path = that._currentPath;
                if (null == path) {
                    return;
                }
                UndoManager.get().add(this, new UndoRedoPair() {
                    @Override public void undo() {
                        that.remove(path);
                    }

                    @Override public void redo() {
                        that.add(path);
                    }
                });
                that._currentPath = null;
            }}, MouseUpEvent.getType()));

        this.registrationsManager.add(this.addDomHandler(new MouseMoveHandler(){
            @Override public void onMouseMove(MouseMoveEvent event) {
                if (null != that._currentPath) {
                    Point2D pos = getMousePositionRelativeToElement(that.getElement());
                    that._currentPath.lineTo(pos.getX(), pos.getY());
                }
            }}, MouseMoveEvent.getType()));
    }

    private Point2D getMousePositionRelativeToElement(final Element that)
    {
        return EventUtils.getCurrentMousePos().minus(ElementUtils.getElementAbsoluteRectangle(that).getCorners().topLeft);
    }

}

