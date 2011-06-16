package com.project.canvas.client.worksheet;

import java.util.HashSet;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.project.canvas.client.canvastools.base.CanvasToolFrame;
import com.project.canvas.client.shared.ElementUtils;
import com.project.canvas.client.shared.NativeUtils;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.client.shared.events.SimpleEvent.Handler;
import com.project.canvas.client.worksheet.interfaces.ElementDragManager.StopCondition;
import com.project.canvas.client.worksheet.interfaces.WorksheetView;
import com.project.canvas.shared.RectangleUtils;
import com.project.canvas.shared.data.Point2D;
import com.project.canvas.shared.data.Rectangle;

public class ToolFrameSelectionManager {
	private ElementDragManagerImpl _selectionDragManager = null;
	private Widget _selectionPanel = null;
	private WorksheetView _worksheetView = null;
	private Widget _container = null;

	public ToolFrameSelectionManager(WorksheetView worksheetView,
			Widget container, Widget dragPanel, Widget selectionPanel,
			SimpleEvent<Void> stopOperationEvent) {
		this._worksheetView = worksheetView;
		this._container = container;
		this._selectionPanel = selectionPanel;
		this._selectionDragManager = new ElementDragManagerImpl(container,
				dragPanel, stopOperationEvent);
	}

	public void handleToolFrameSelection(CanvasToolFrame toolFrame) {
		if (isMultiSelect()) {
			this.toggleToolFrameSelection(toolFrame);
		} else {
			this._worksheetView.clearToolFrameSelection();
			this._worksheetView.selectToolFrame(toolFrame);
		}
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

		final HashSet<CanvasToolFrame> newlySelectedFrames = new HashSet<CanvasToolFrame>();
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
				for (CanvasToolFrame toolFrame : newlySelectedFrames) {
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
			HashSet<CanvasToolFrame> selectedFrameSet) {
		for (CanvasToolFrame toolFrame : this._worksheetView.getToolFrames()) {
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
	private void toggleToolFrameSelection(CanvasToolFrame toolFrame) {
		if (this._worksheetView.isToolFrameSelected(toolFrame)) {
			this._worksheetView.unSelectToolFrame(toolFrame);
		} else {
			this._worksheetView.selectToolFrame(toolFrame);
		}
	}
}
