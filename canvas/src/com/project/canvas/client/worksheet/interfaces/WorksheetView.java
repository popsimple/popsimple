package com.project.canvas.client.worksheet.interfaces;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.project.canvas.client.canvastools.base.CanvasTool;
import com.project.canvas.client.canvastools.base.CanvasToolFactory;
import com.project.canvas.client.canvastools.base.CanvasToolFrame;
import com.project.canvas.client.canvastools.base.ToolboxItem;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.shared.data.CanvasPageOptions;
import com.project.canvas.shared.data.ElementData;
import com.project.canvas.shared.data.Point2D;
import com.project.canvas.shared.data.Transform2D;

public interface WorksheetView extends IsWidget
{

    public enum OperationStatus {
        FAILURE, PENDING, SUCCESS,
    }

    public class ToolCreationRequest
    {
        private final CanvasToolFactory<? extends CanvasTool<? extends ElementData>> factory;

        private final Point2D position;
        public ToolCreationRequest(Point2D position,
                CanvasToolFactory<? extends CanvasTool<? extends ElementData>> factory)
        {
            this.position = position;
            this.factory = factory;
        }

        public CanvasToolFactory<? extends CanvasTool<? extends ElementData>> getFactory()
        {
            return factory;
        }

        public Point2D getPosition()
        {
            return position;
        }
    }

    /**
     * Fired when the user requests to load a new page or reload the existing
     * page
     * 
     * @param handler
     *            - the Long (id) argument will be null if the user wants to
     *            reload existing page.
     * @return
     */
    HandlerRegistration addLoadHandler(SimpleEvent.Handler<String> handler);

    HandlerRegistration addOptionsUpdatedHandler(SimpleEvent.Handler<CanvasPageOptions> handler);

    HandlerRegistration addSaveHandler(SimpleEvent.Handler<Void> handler);

    HandlerRegistration addStopOperationHandler(SimpleEvent.Handler<Void> handler);

    HandlerRegistration addToolCreationRequestHandler(SimpleEvent.Handler<ToolCreationRequest> handler);

    /**
     * The handler will be fired when the user clicks in the region of a tool frame.
     * @param handler
     * @return
     */
    HandlerRegistration addToolFrameClickHandler(SimpleEvent.Handler<CanvasToolFrame> handler);
    
    void addToolInstanceWidget(final CanvasToolFrame toolFrame, Transform2D transform, Point2D additionalOffset);

    HandlerRegistration addViewHandler(SimpleEvent.Handler<Void> handler);

    void clearActiveToolboxItem();

    void onLoadOperationChange(OperationStatus status, String reason);

    void onSaveOperationChange(OperationStatus status, String reason);

    void removeToolInstanceWidget(CanvasToolFrame widget);

    void setActiveToolboxItem(ToolboxItem toolboxItem);

    void setOptions(CanvasPageOptions options);

    void setViewMode(boolean isViewMode);
}
