package com.project.website.canvas.client.worksheet;

import java.util.HashSet;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.client.utils.ElementUtils;
import com.project.shared.client.utils.NativeUtils;
import com.project.shared.data.Point2D;
import com.project.shared.data.Rectangle;
import com.project.shared.utils.RectangleUtils;
import com.project.website.canvas.client.canvastools.base.CanvasToolFrameImpl;
import com.project.website.canvas.client.worksheet.interfaces.ElementDragManager.StopCondition;
import com.project.website.canvas.client.worksheet.interfaces.WorksheetView;

public class ToolFrameSelectionManager {
	private ElementDragManagerImpl _selectionDragManager = null;
	private Widget _selectionPanel = null;
	private WorksheetView _worksheetView = null;
	private Widget _container = null;
	private HashSet<CanvasToolFrameImpl> _ignoreSet = new HashSet<CanvasToolFrameImpl>();

	public ToolFrameSelectionManager(WorksheetView worksheetView,
			Widget container, Widget dragPanel, Widget selectionPanel,
			SimpleEvent<Void> stopOperationEvent) {
		this._worksheetView = worksheetView;
		this._container = container;
		this._selectionPanel = selectionPanel;
		this._selectionDragManager = new ElementDragManagerImpl(container,
				dragPanel, stopOperationEvent);
	}

	public void handleToolFrameSelection(CanvasToolFrameImpl toolFrame) {
		if (this._ignoreSet.remove(toolFrame))
        {
		    return;
		}
		if (isMultiSelect()) {
            this.toggleToolFrameSelection(toolFrame);
        } else {
            this._worksheetView.clearToolFrameSelection();
            this._worksheetView.selectToolFrame(toolFrame);
        }
	}

	/**
     * Forces a selection of a specific tool frame.
     * Should be called in case a selection is required prior to the usual flow of the
     * selection manager handling (MouseUp). this method makes sure that when the regular
     * handler will be fired, this tool frame will be ignored.
     *
     * @param toolFrame the toolframe to select.
     */
	public void forceToolFrameSelection(CanvasToolFrameImpl toolFrame)
	{
	    this._ignoreSet.remove(toolFrame);
	    if (false == this.ensureToolFrameSelection(toolFrame))
	    {
	        return;
	    }
	    this._ignoreSet.add(toolFrame);
	}

	public boolean ensureToolFrameSelection(CanvasToolFrameImpl toolFrame)
	{
	    if (this._worksheetView.isToolFrameSelected(toolFrame)) {
            return false;
        }
	    this.handleToolFrameSelection(toolFrame);
	    return true;
	}

	public void startSelectionDrag(MouseDownEvent event) {
	    NativeUtils.disableTextSelectInternal(_container.getElement(), true);

		if (false == event.isControlKeyDown()) {
			this._worksheetView.clearToolFrameSelection();
		}
		final Point2D initialPosition =
			ElementUtils.relativePosition(event, this._container.getElement());
		ElementUtils.setElementRectangle(this._selectionPanel.getElement(),
				new Rectangle(initialPosition.getX(), initialPosition.getY(), 0));

		final HashSet<CanvasToolFrameImpl> newlySelectedFrames = new HashSet<CanvasToolFrameImpl>();
		this._selectionPanel.setVisible(true);

		Handler<Point2D> mouseMoveHandler = new Handler<Point2D>() {
			@Override
			public void onFire(Point2D arg) {
				Element selectionElement = _selectionPanel.getElement();
				ElementUtils.setElementRectangle(selectionElement,
						RectangleUtils.Build(initialPosition, arg));

				selectFramesByRectangle(ElementUtils.getElementOffsetRectangle(
						selectionElement), newlySelectedFrames);
			}
		};
		Handler<Point2D> stopHandler = new Handler<Point2D>() {
			@Override
			public void onFire(Point2D arg) {
				hideSelectionPanel();
			}
		};
		Handler<Void> cancelHandler = new Handler<Void>() {
			@Override
			public void onFire(Void arg) {
				hideSelectionPanel();
				for (CanvasToolFrameImpl toolFrame : newlySelectedFrames) {
					_worksheetView.unSelectToolFrame(toolFrame);
				}
			}
		};

		this._selectionDragManager.startMouseMoveOperation(this._container.getElement(),
		        Point2D.zero, mouseMoveHandler, stopHandler, cancelHandler,
				StopCondition.STOP_CONDITION_MOUSE_UP);
	}

	private void hideSelectionPanel() {
		this._selectionPanel.setVisible(false);
		ElementUtils.setElementSize(this._selectionPanel.getElement(), Point2D.zero);
	}

	private void selectFramesByRectangle(Rectangle selectionRectangle,
			HashSet<CanvasToolFrameImpl> selectedFrameSet) {
		for (CanvasToolFrameImpl toolFrame : this._worksheetView.getToolFrames()) {
			if (selectionRectangle.isOverlapping(ElementUtils
					.getElementOffsetRectangle(toolFrame.getElement()))) {
				if (false == this._worksheetView.isToolFrameSelected(toolFrame)) {
					selectedFrameSet.add(toolFrame);
					this._worksheetView.selectToolFrame(toolFrame);
				}
			} else {
				if (selectedFrameSet.contains(toolFrame)) {
					this._worksheetView.unSelectToolFrame(toolFrame);
					selectedFrameSet.remove(toolFrame);
				}
			}
		}
	}

	private boolean isMultiSelect() {
		Event event = Event.getCurrentEvent();
		if (null == event) {
			return false;
		}
		// TODO: Handle special keys for mac OS.
		if (event.getCtrlKey()) {
			return true;
		}
		return false;
	}

	// TODO: Should be here or in worksheetView?
	private void toggleToolFrameSelection(CanvasToolFrameImpl toolFrame) {
		if (this._worksheetView.isToolFrameSelected(toolFrame)) {
			this._worksheetView.unSelectToolFrame(toolFrame);
		} else {
			this._worksheetView.selectToolFrame(toolFrame);
		}
	}
}
