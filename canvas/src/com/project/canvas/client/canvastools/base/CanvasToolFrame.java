package com.project.canvas.client.canvastools.base;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.project.canvas.client.shared.NativeUtils;
import com.project.canvas.client.shared.WidgetUtils;
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
	Anchor moveBackLink;
	
	@UiField
	Anchor moveFrontLink;

	@UiField
	HTMLPanel framePanel;

	@UiField
	FlowPanel buttonsPanel;
	
	@UiField
	HTMLPanel bottomRightResizePanel;
	
	protected final CanvasTool<?> tool;
	
	protected final SimpleEvent<Void> closeRequest = new SimpleEvent<Void>();
	protected final SimpleEvent<Void> moveBackRequest = new SimpleEvent<Void>();
	protected final SimpleEvent<Void> moveFrontRequest = new SimpleEvent<Void>();
	protected final SimpleEvent<MouseEvent<?>> moveStartRequest = new SimpleEvent<MouseEvent<?>>();
	protected final SimpleEvent<MouseEvent<?>> resizeStartRequest = new SimpleEvent<MouseEvent<?>>();

	public CanvasToolFrame(CanvasTool<?> canvasTool) {
		initWidget(uiBinder.createAndBindUi(this));
		WidgetUtils.stopClickPropagation(this);
		this.tool = canvasTool;
		this.toolPanel.add(canvasTool);
		this.closeLink.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				closeRequest.dispatch(null);
				event.stopPropagation();
			}
		});
		this.moveBackLink.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				moveBackRequest.dispatch(null);
				event.stopPropagation();
			}
		});
		this.moveFrontLink.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				moveFrontRequest.dispatch(null);
				event.stopPropagation();
			}
		});
		
		this.framePanel.addDomHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				moveStartRequest.dispatch(event);
				event.stopPropagation();
			}
		},MouseDownEvent.getType());

		canvasTool.addMoveStartEventHandler(new SimpleEvent.Handler<MouseEvent<?>>() {
			@Override
			public void onFire(MouseEvent<?> arg) {
				moveStartRequest.dispatch(arg);
			}
		});
		
		this.registerResizeHandlers();
		
//		CanvasToolCommon.stopClickPropagation(buttonsPanel);
		WidgetUtils.stopClickPropagation(closeLink);
		WidgetUtils.stopClickPropagation(moveBackLink);
		WidgetUtils.stopClickPropagation(moveFrontLink);
		NativeUtils.disableTextSelectInternal(this.buttonsPanel.getElement(), true);
	}
	
	protected void registerResizeHandlers()
	{
		this.bottomRightResizePanel.addDomHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				resizeStartRequest.dispatch(event);
				event.stopPropagation();
			}}, MouseDownEvent.getType());
	}
	
	public CanvasTool<?> getTool() {
		return this.tool;
	}

	public SimpleEvent<Void> getCloseRequest() {
		return closeRequest;
	}

	public SimpleEvent<MouseEvent<?>> getMoveStartRequest() {
		return moveStartRequest;
	}

	public HandlerRegistration addMoveBackRequestHandler(
			SimpleEvent.Handler<Void> handler)
	{
		return this.moveBackRequest.addHandler(handler);
	}
	
	public HandlerRegistration addMoveFrontRequestHandler(
			SimpleEvent.Handler<Void> handler)
	{
		return this.moveFrontRequest.addHandler(handler);
	}
	
	public HandlerRegistration addResizeStartRequestHandler(
			SimpleEvent.Handler<MouseEvent<?>> handler)
	{
		return this.resizeStartRequest.addHandler(handler);
	}
	
	public Point2D getToolSize()
	{
		return new Point2D(this.tool.asWidget().getOffsetWidth(), 
				this.tool.asWidget().getOffsetHeight()); 
	}
	
	public void setToolSize(Point2D size)
	{
		this.tool.asWidget().getElement().getStyle().setWidth(size.getX(), Unit.PX);
		this.tool.asWidget().getElement().getStyle().setHeight(size.getY(), Unit.PX);
	}
}
