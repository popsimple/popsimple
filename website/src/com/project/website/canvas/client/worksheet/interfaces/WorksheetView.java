package com.project.website.canvas.client.worksheet.interfaces;

import java.util.ArrayList;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.data.Point2D;
import com.project.website.canvas.client.canvastools.base.CanvasTool;
import com.project.website.canvas.client.canvastools.base.CanvasToolFactory;
import com.project.website.canvas.client.canvastools.base.CanvasToolFrame;
import com.project.website.canvas.client.canvastools.base.ToolboxItem;
import com.project.website.canvas.shared.data.CanvasPageOptions;
import com.project.website.canvas.shared.data.ElementData;
import com.project.website.canvas.shared.data.Transform2D;
import com.project.website.shared.data.UserProfile;

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

    HandlerRegistration addLogoutHandler(Handler<Void> handler);

    HandlerRegistration addInviteHandler(Handler<Void> handler);

    HandlerRegistration addStopOperationHandler(SimpleEvent.Handler<Void> handler);

    HandlerRegistration addToolCreationRequestHandler(SimpleEvent.Handler<ToolCreationRequest> handler);

    /**
     * The handler will be fired when the user clicks the worksheet, and gives the tool frame
     * clicked or no tool frame (null) if none are in the region of the click.
     * @param handler
     * @return
     */
    HandlerRegistration addToolFrameClickHandler(SimpleEvent.Handler<CanvasToolFrame> handler);

    HandlerRegistration addRemoveToolsRequest(SimpleEvent.Handler<ArrayList<CanvasToolFrame>> handler);

    HandlerRegistration addCopyToolHandler(SimpleEvent.Handler<ArrayList<CanvasToolFrame>> handler);

    HandlerRegistration addPasteToolHandler(SimpleEvent.Handler<Void> handler);

    /**
     * Adds a new toolFrame to the view. The view will call setToolFrameTransform by itself then the frame is attached and ready to transform.
     * @param toolFrame
     * @param transform
     * @param additionalOffset
     */
    void addToolInstanceWidget(final CanvasToolFrame toolFrame, Transform2D transform, Point2D additionalOffset);

    HandlerRegistration addViewHandler(SimpleEvent.Handler<Void> handler);

    void clearActiveToolboxItem();

    void onLoadOperationChange(OperationStatus status, String reason);

    void onSaveOperationChange(OperationStatus status, String reason);

    void removeToolInstanceWidget(CanvasToolFrame widget);

    void setActiveToolboxItem(ToolboxItem toolboxItem);

    void setOptions(CanvasPageOptions options);

    void setViewMode(boolean isViewMode);

    void setToolFrameTransform(CanvasToolFrame toolFrame, Transform2D transform, Point2D additionalOffset);

    void selectToolFrame(CanvasToolFrame widget);

    void unSelectToolFrame(CanvasToolFrame widget);

    boolean isToolFrameSelected(CanvasToolFrame toolFrame);

    void clearToolFrameSelection();

    void setUserProfile(UserProfile result);

    ArrayList<CanvasToolFrame> getToolFrames();

}
