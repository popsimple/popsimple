package com.project.website.canvas.client.canvastools.sketch;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.shape.Path;

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
import com.project.shared.data.Point2D;
import com.project.website.canvas.client.canvastools.base.CanvasTool;
import com.project.website.canvas.shared.data.ElementData;
import com.project.website.canvas.shared.data.VectorGraphicsData;

public class SketchTool extends DrawingArea implements CanvasTool<VectorGraphicsData> 
{
    private final static int defaultWidth = 200;
    private final static int defaultHeight = 200;
    
    public SketchTool() {
        super(defaultWidth, defaultHeight);
    }

    private final RegistrationsManager registrationsManager = new RegistrationsManager();
    protected Path _currentPath;
    
    @Override
    protected void onLoad() {
        super.onLoad();
        
        final SketchTool that = this;
        this.registrationsManager.add(this.addDomHandler(new MouseDownHandler(){
            @Override public void onMouseDown(MouseDownEvent event) {
                that._currentPath = new Path(event.getClientX(), event.getClientY());
                that.add(that._currentPath);
            }}, MouseDownEvent.getType()));
        
        this.registrationsManager.add(this.addDomHandler(new MouseUpHandler(){
            @Override public void onMouseUp(MouseUpEvent event) {
                that._currentPath = null;
            }}, MouseUpEvent.getType()));
        
        this.registrationsManager.add(this.addDomHandler(new MouseMoveHandler(){
            @Override public void onMouseMove(MouseMoveEvent event) {
                if (null != that._currentPath) {
                    that._currentPath.lineTo(event.getClientX(), event.getClientY());
                }
            }}, MouseMoveEvent.getType()));
    }

    @Override
    protected void onUnload() {
        super.onUnload();
    }

    @Override
    public void setValue(VectorGraphicsData value) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public VectorGraphicsData getValue() {
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setActive(boolean isActive) {
        // TODO Auto-generated method stub
        
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
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setViewMode(boolean isViewMode) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onResize() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public IsWidget getToolbar() {
        // TODO Auto-generated method stub
        return null;
    }
}
