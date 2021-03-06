package com.project.website.canvas.client.worksheet.interfaces;

import java.util.ArrayList;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.data.Point2D;
import com.project.website.canvas.client.canvastools.base.interfaces.CanvasTool;
import com.project.website.canvas.client.canvastools.base.interfaces.CanvasToolFactory;
import com.project.website.canvas.client.canvastools.base.interfaces.CanvasToolFrame;
import com.project.website.canvas.client.canvastools.base.interfaces.ToolboxItem;
import com.project.website.canvas.shared.data.CanvasPageOptions;
import com.project.website.canvas.shared.data.ElementData;
import com.project.website.canvas.shared.data.Transform2D;
import com.project.website.shared.data.UserProfile;

public interface WorksheetView extends IsWidget
{

    public enum OperationStatus {
        FAILURE, PENDING, SUCCESS,
    }

    public class ImageDropInfo {
    	private final String dataUrl;
    	private final Point2D position;
		public String getDataUrl() {
			return dataUrl;
		}
		public Point2D getPosition() {
			return position;
		}
		public ImageDropInfo(String dataUrl, Point2D position) {
			super();
			this.dataUrl = dataUrl;
			this.position = position;
		}
    	
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


        /**
         * Override in subclasses to do something after the tool was created.
         */
        public void toolCreated(CanvasTool<? extends ElementData> tool) { }
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

    HandlerRegistration addNewPageHandler(Handler<Void> handler);
    
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
    HandlerRegistration addActiveToolFrameChangedHandler(SimpleEvent.Handler<CanvasToolFrame> handler);

    HandlerRegistration addRemoveToolsRequest(SimpleEvent.Handler<ArrayList<CanvasToolFrame>> handler);

    HandlerRegistration addCopyToolHandler(SimpleEvent.Handler<ArrayList<CanvasToolFrame>> handler);

    HandlerRegistration addPasteToolHandler(SimpleEvent.Handler<Void> handler);
    
    /**
     * Triggered when the user drops an image file onto the worksheet using drag-and-drop
     * The DataUrl-format image binary data is passed as the String argument to the handler
     */
    HandlerRegistration addImageDropHandler(SimpleEvent.Handler<ImageDropInfo> handler);

    HandlerRegistration addUndoRequestHandler(SimpleEvent.Handler<Void> handler);

    HandlerRegistration addAddSpaceHandler(Handler<Void> handler);
    
    /**
     * Adds a new toolFrame to the view. The view will call setToolFrameTransform by itself then the frame is attached and ready to transform.
     * @param toolFrame
     * @param transform
     * @param additionalOffset
     */
    void addToolInstanceWidget(final CanvasToolFrame toolFrame, Transform2D transform, Point2D additionalOffset, boolean addFrameInnerOffset);

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

    void setViewLinkTargetHistoryToken(String targetHistoryToken);

    void setPageEditable(boolean isEditable);

    void pageSizeUpdated();
}
