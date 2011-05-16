package com.project.canvas.client.canvastools.base;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.project.canvas.client.shared.NativeUtils;
import com.project.canvas.client.shared.events.SimpleEvent;

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
	protected final SimpleEvent<MouseDownEvent> moveStartRequest = new SimpleEvent<MouseDownEvent>();
	protected final SimpleEvent<MouseDownEvent> resizeStartRequest = new SimpleEvent<MouseDownEvent>();

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
		},
		MouseDownEvent.getType());
		
		this.registerResizeHandlers();
		
//		CanvasToolCommon.stopClickPropagation(buttonsPanel);
		CanvasToolCommon.stopClickPropagation(closeLink);
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

	public SimpleEvent<MouseDownEvent> getMoveStartRequest() {
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
			SimpleEvent.Handler<MouseDownEvent> handler)
	{
		return this.resizeStartRequest.addHandler(handler);
	}
	
	public void setHeight(double value)
	{
		this.tool.asWidget().getElement().getStyle().setHeight(value -
				(this.getOffsetHeight() -
				this.tool.asWidget().getOffsetHeight()), Unit.PX);
	}
	
	public void setWidth(double value)
	{
		this.tool.asWidget().getElement().getStyle().setWidth(value -
				(this.getOffsetWidth() -
				this.tool.asWidget().getOffsetWidth()), Unit.PX);
	}
}
