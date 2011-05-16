package com.project.canvas.client.canvastools.base;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.project.canvas.client.shared.NativeUtils;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.shared.data.Point2D;

public class CanvasToolFrame extends Composite {

	private static CanvasToolFrameUiBinder uiBinder = GWT
			.create(CanvasToolFrameUiBinder.class);

	interface CanvasToolFrameUiBinder extends UiBinder<Widget, CanvasToolFrame> {
	}

	@UiField
	HTMLPanel toolPanel;
	
	@UiField
	Anchor closeLink;

	@UiField
	HTMLPanel framePanel;

	@UiField
	FlowPanel buttonsPanel;
	
	@UiField
	HTMLPanel bottomRightResizePanel;
	
	protected final CanvasTool<?> tool;
	
	protected final SimpleEvent<Void> closeRequest = new SimpleEvent<Void>();
	protected final SimpleEvent<MouseDownEvent> moveStartRequest = new SimpleEvent<MouseDownEvent>();

	private int resizeTopStart;

	private int resizeLeftStart;

	public CanvasToolFrame(CanvasTool<?> canvasTool) {
		initWidget(uiBinder.createAndBindUi(this));
		CanvasToolCommon.stopClickPropagation(this);
		this.tool = canvasTool;
		this.toolPanel.add(canvasTool);
		this.closeLink.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				closeRequest.dispatch(null);
				event.stopPropagation();
			}
		});
		this.framePanel.addDomHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				moveStartRequest.dispatch(event);
				event.stopPropagation();
			}
		},
		MouseDownEvent.getType());
		CanvasToolCommon.stopClickPropagation(buttonsPanel);
		NativeUtils.disableTextSelectInternal(this.buttonsPanel.getElement(), true);
		
		this.registerResizeHandlers();
	}
	
	protected void registerResizeHandlers()
	{
		this.bottomRightResizePanel.addDomHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				mouseDownBottomRightResize(event);
			}}, MouseDownEvent.getType());
	}
	
	protected void mouseDownBottomRightResize(MouseDownEvent event)
	{
		this.addDomHandler(new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				mouseMoveBottomRightResize(event);
			}}, MouseMoveEvent.getType());
	}
	
	protected void mouseMoveBottomRightResize(MouseMoveEvent event)
	{
		this.tool.asWidget().getElement().getStyle().setWidth(
				event.getRelativeX(this.tool.asWidget().getElement()), Unit.PX);
		this.tool.asWidget().getElement().getStyle().setHeight(
				event.getRelativeY(this.tool.asWidget().getElement()), Unit.PX);
	}
	
	public CanvasTool<?> getTool() {
		return this.tool;
	}

	public SimpleEvent<Void> getCloseRequest() {
		return closeRequest;
	}

	public SimpleEvent<MouseDownEvent> getMoveStartRequest() {
		return moveStartRequest;
	}

}
