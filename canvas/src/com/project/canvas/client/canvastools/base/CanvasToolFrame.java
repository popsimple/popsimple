package com.project.canvas.client.canvastools.base;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasBlurHandlers;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.project.canvas.client.canvastools.base.CanvasTool.ResizeMode;
import com.project.canvas.client.shared.ElementUtils;
import com.project.canvas.client.shared.NativeUtils;
import com.project.canvas.client.shared.RegistrationsManager;
import com.project.canvas.client.shared.WidgetUtils;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.client.shared.events.SimpleEvent.Handler;
import com.project.canvas.shared.data.ElementData;
import com.project.canvas.shared.data.Point2D;

public class CanvasToolFrame extends Composite implements Focusable, HasFocusHandlers, HasBlurHandlers {

    private static CanvasToolFrameUiBinder uiBinder = GWT.create(CanvasToolFrameUiBinder.class);

    interface CanvasToolFrameUiBinder extends UiBinder<Widget, CanvasToolFrame> {
    }

    @UiField
    FocusPanel focusPanel;

    @UiField
    HTMLPanel frameHeader;

    @UiField
    HTMLPanel toolPanel;

    @UiField
    Anchor closeLink;

    @UiField
    Anchor moveBackLink;

    @UiField
    Anchor moveFrontLink;

    @UiField
    FlowPanel framePanel;

    @UiField
    FlowPanel buttonsPanel;

    @UiField
    HTMLPanel resizePanel;

    @UiField
    HTMLPanel rotatePanel;

    protected final CanvasTool<?> tool;

    protected final SimpleEvent<Void> closeRequest = new SimpleEvent<Void>();
    protected final SimpleEvent<Void> moveBackRequest = new SimpleEvent<Void>();
    protected final SimpleEvent<Void> moveFrontRequest = new SimpleEvent<Void>();
    protected final SimpleEvent<MouseEvent<?>> moveStartRequest = new SimpleEvent<MouseEvent<?>>();
    protected final SimpleEvent<MouseEvent<?>> resizeStartRequest = new SimpleEvent<MouseEvent<?>>();
    protected final SimpleEvent<MouseEvent<?>> rotateStartRequest = new SimpleEvent<MouseEvent<?>>();
    protected final SimpleEvent<Void> selectRequest = new SimpleEvent<Void>();

    protected final RegistrationsManager frameRegs = new RegistrationsManager();

	protected Integer rotation;
	private boolean viewMode = false;

    public CanvasToolFrame(CanvasTool<?> canvasTool) {
        initWidget(uiBinder.createAndBindUi(this));
        //WidgetUtils.stopClickPropagation(this);
        this.tool = canvasTool;
        this.toolPanel.add(canvasTool);
        this.reRegisterFrameHandlers();
        this.registerTransformHandlers();

        WidgetUtils.stopClickPropagation(this.closeLink.asWidget());
        WidgetUtils.stopClickPropagation(this.moveBackLink.asWidget());
        WidgetUtils.stopClickPropagation(this.moveFrontLink.asWidget());

        NativeUtils.disableTextSelectInternal(this.buttonsPanel.getElement(), true);

        this.rotatePanel.setVisible(tool.canRotate());
        this.resizePanel.setVisible(tool.getResizeMode() != ResizeMode.NONE);
    }

	private void reRegisterFrameHandlers() {
		frameRegs.clear();
		frameRegs.add(this.closeLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                closeRequest.dispatch(null);
            }
        }));
		frameRegs.add(this.moveBackLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                moveBackRequest.dispatch(null);
            }
        }));
		frameRegs.add(this.moveFrontLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                moveFrontRequest.dispatch(null);
            }
        }));
		frameRegs.add(this.resizePanel.addDomHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                resizeStartRequest.dispatch(event);
            }
        }, MouseDownEvent.getType()));
		frameRegs.add(this.rotatePanel.addDomHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                rotateStartRequest.dispatch(event);
            }
        }, MouseDownEvent.getType()));

		frameRegs.add(this.frameHeader.addDomHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(final MouseDownEvent event) {
                onHeaderMouseDown(event);
            }
        }, MouseDownEvent.getType()));
		frameRegs.add(tool.addSelfMoveRequestEventHandler(new Handler<Point2D>() {
			@Override
			public void onFire(Point2D offset) {
				toolSelfMoveRequest(offset);
			}
		}));
		frameRegs.add(this.addDomHandler(new MouseDownHandler(){
			@Override
			public void onMouseDown(MouseDownEvent event) {
			    onToolFrameSelected(event);
			}}, MouseDownEvent.getType()));
	}

	private void onToolFrameSelected(MouseDownEvent event)
	{
	    this.selectRequest.dispatch(null);
	    focusPanel.setFocus(true); // take away focus from any others
	}

	private void onHeaderMouseDown(MouseDownEvent event)
	{
	    moveStartRequest.dispatch(event);
	}

    protected void registerTransformHandlers() {
        this.tool.addMoveStartEventHandler(new SimpleEvent.Handler<MouseEvent<?>>() {
            @Override
            public void onFire(MouseEvent<?> arg) {
                moveStartRequest.dispatch(arg);
            }
        });
    }

    public void setViewMode(boolean inViewMode) {
    	if (inViewMode == this.viewMode) {
    		return;
    	}
    	this.viewMode = inViewMode;
    	if (this.viewMode) {
    		frameRegs.clear();
    	}
    	else {
    		this.reRegisterFrameHandlers();
    	}
    	this.tool.setViewMode(this.viewMode);
    }

    public CanvasTool<? extends ElementData> getTool() {
        return this.tool;
    }

    public HandlerRegistration addCloseRequestHandler(SimpleEvent.Handler<Void> handler) {
        return this.closeRequest.addHandler(handler);
    }

    public HandlerRegistration addMoveStartRequestHandler(SimpleEvent.Handler<MouseEvent<?>> handler) {
        return this.moveStartRequest.addHandler(handler);
    }

    public HandlerRegistration addMoveBackRequestHandler(SimpleEvent.Handler<Void> handler) {
        return this.moveBackRequest.addHandler(handler);
    }

    public HandlerRegistration addMoveFrontRequestHandler(SimpleEvent.Handler<Void> handler) {
        return this.moveFrontRequest.addHandler(handler);
    }

    public HandlerRegistration addResizeStartRequestHandler(SimpleEvent.Handler<MouseEvent<?>> handler) {
        return this.resizeStartRequest.addHandler(handler);
    }

    public HandlerRegistration addRotateStartRequestHandler(SimpleEvent.Handler<MouseEvent<?>> handler) {
        return this.rotateStartRequest.addHandler(handler);
    }

    public HandlerRegistration addMouseDownHandler(MouseDownHandler handler)
    {
        return this.addDomHandler(handler, MouseDownEvent.getType());
    }

    public HandlerRegistration addMouseUpHandler(MouseUpHandler handler)
    {
        return this.addDomHandler(handler, MouseUpEvent.getType());
    }

    public Point2D getToolSize() {
        return ElementUtils.getElementClientSize(this.tool.asWidget().getElement());
    }

    /**
     * Note: make sure the size here does NOT include padding/margin/border of the tool, otherwise
     * getToolSize and setToolSize will not be compatible (will be using different values.)
     */
    public void setToolSize(Point2D size) {
    	ResizeMode resizeMode = this.tool.getResizeMode();
    	if (ResizeMode.NONE == resizeMode) {
    		return;
    	}
        if (ResizeMode.BOTH  == resizeMode || ResizeMode.WIDTH_ONLY == resizeMode) {
            this.tool.asWidget().getElement().getStyle().setWidth(size.getX(), Unit.PX);
        }
        if (ResizeMode.BOTH  == resizeMode || ResizeMode.HEIGHT_ONLY == resizeMode) {
            this.tool.asWidget().getElement().getStyle().setHeight(size.getY(), Unit.PX);
        }
        if (ResizeMode.RELATIVE == resizeMode) {
        	int uniformSize = (size.getX() + size.getY()) / 2;
        	WidgetUtils.setWidgetSize(this.tool.asWidget(), new Point2D(uniformSize, uniformSize));
        }
    }

	@Override
	public int getTabIndex() {
		return this.focusPanel.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		this.focusPanel.setAccessKey(key);
	}

	@Override
	public void setFocus(boolean focused) {
		this.focusPanel.setFocus(focused);
	}

	@Override
	public void setTabIndex(int index) {
		this.focusPanel.setTabIndex(index);
	}

	@Override
	public HandlerRegistration addBlurHandler(BlurHandler handler) {
		return this.focusPanel.addBlurHandler(handler);
	}

	@Override
	public HandlerRegistration addFocusHandler(FocusHandler handler) {
		return this.focusPanel.addFocusHandler(handler);
	}

	private void toolSelfMoveRequest(Point2D offset) {
		// TODO find a more global way of handling view mode.
		if (this.viewMode) {
			return;
		}
		Point2D newPos = ElementUtils.getElementOffsetPosition(getElement()).plus(offset);
		ElementUtils.setElementPosition(getElement(), newPos);
	}
}
