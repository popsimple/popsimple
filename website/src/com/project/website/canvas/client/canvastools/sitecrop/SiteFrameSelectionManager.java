package com.project.website.canvas.client.canvastools.sitecrop;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.client.utils.ElementUtils;
import com.project.shared.client.utils.NativeUtils;
import com.project.shared.data.Point2D;
import com.project.shared.data.Rectangle;
import com.project.shared.utils.RectangleUtils;
import com.project.website.canvas.client.worksheet.ElementDragManagerImpl;
import com.project.website.canvas.client.worksheet.interfaces.ElementDragManager.StopCondition;

public class SiteFrameSelectionManager {
    public final static int MIN_VALID_SELECTION_SIZE_PX = 5;

    private ElementDragManagerImpl _selectionDragManager = null;
	private Widget _selectionPanel = null;
	private Widget _container = null;

	public SiteFrameSelectionManager(Widget container, Widget dragPanel,
	        Widget selectionPanel, SimpleEvent<Void> stopOperationEvent) {
		this._container = container;
		this._selectionPanel = selectionPanel;
		this._selectionDragManager = new ElementDragManagerImpl(container,
				dragPanel, stopOperationEvent);
	}

	public void startSelectionDrag(MouseDownEvent event) {
	    NativeUtils.disableTextSelectInternal(_container.getElement(), true);

		final Point2D initialPosition =
			ElementUtils.getRelativePosition(event, this._container.getElement());
		ElementUtils.setElementRectangle(this._selectionPanel.getElement(),
				new Rectangle(initialPosition.getX(), initialPosition.getY(), 0));

		this._selectionPanel.setVisible(true);

		Handler<Point2D> mouseMoveHandler = new Handler<Point2D>() {
			@Override
			public void onFire(Point2D arg) {
				Element selectionElement = _selectionPanel.getElement();
				ElementUtils.setElementRectangle(selectionElement,
						RectangleUtils.Build(initialPosition, arg));
			}
		};
		Handler<Point2D> stopHandler = new Handler<Point2D>() {
			@Override
			public void onFire(Point2D arg) {
			    if (false == isValidSelection())
			    {
			        clearSelection();
			    }
			}
		};
		Handler<Void> cancelHandler = new Handler<Void>() {
			@Override
			public void onFire(Void arg) {
			    clearSelection();
			}
		};

		this._selectionDragManager.startMouseMoveOperation(this._container.getElement(),
		        Point2D.zero, mouseMoveHandler, stopHandler, cancelHandler,
				StopCondition.STOP_CONDITION_MOUSE_UP);
	}

	public Rectangle getSelectedRectangle()
	{
	    return ElementUtils.getElementOffsetRectangle(_selectionPanel.getElement());
	}

	public void clearSelection() {
		this._selectionPanel.setVisible(false);
		ElementUtils.setElementSize(this._selectionPanel.getElement(), Point2D.zero);
	}

	private boolean isValidSelection()
	{
	    Point2D selectionSize = this.getSelectedRectangle().getSize();
	    if ((selectionSize.getX() > MIN_VALID_SELECTION_SIZE_PX) &&
	        (selectionSize.getY() > MIN_VALID_SELECTION_SIZE_PX))
	    {
	        return true;
	    }
	    return false;
	}
}
