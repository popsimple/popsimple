package com.project.website.canvas.client.canvastools.base;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.client.handlers.RegistrationsManager;
import com.project.shared.client.utils.ElementUtils;
import com.project.shared.client.utils.NativeUtils;
import com.project.shared.client.utils.SchedulerUtils;
import com.project.shared.client.utils.widgets.WidgetUtils;
import com.project.shared.data.Point2D;
import com.project.website.canvas.client.canvastools.base.CanvasTool.ResizeMode;
import com.project.website.canvas.client.resources.CanvasResources;
import com.project.website.canvas.client.shared.widgets.FloatingToolbar;
import com.project.website.canvas.shared.data.ElementData;

public class CanvasToolFrameImpl extends Composite implements CanvasToolFrame {

    private static CanvasToolFrameImplUiBinder uiBinder = GWT.create(CanvasToolFrameImplUiBinder.class);

    interface CanvasToolFrameImplUiBinder extends UiBinder<Widget, CanvasToolFrameImpl> {
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

    private FloatingToolbar floatingToolbar = null;

    protected final SimpleEvent<Void> closeRequest = new SimpleEvent<Void>();
    protected final SimpleEvent<Void> moveBackRequest = new SimpleEvent<Void>();
    protected final SimpleEvent<Void> moveFrontRequest = new SimpleEvent<Void>();
    protected final SimpleEvent<MouseEvent<?>> moveStartRequest = new SimpleEvent<MouseEvent<?>>();
    protected final SimpleEvent<MouseEvent<?>> resizeStartRequest = new SimpleEvent<MouseEvent<?>>();
    protected final SimpleEvent<MouseEvent<?>> rotateStartRequest = new SimpleEvent<MouseEvent<?>>();
    protected final SimpleEvent<Void> selectRequest = new SimpleEvent<Void>();

    private final RegistrationsManager frameRegs = new RegistrationsManager();
    private final RegistrationsManager toolRegs = new RegistrationsManager();

	protected Integer _rotation = null;
    private int draggingStackDepth = 0;
	private boolean _viewMode = false;
    private boolean _isActive = false;


    public CanvasToolFrameImpl(CanvasTool<?> canvasTool) {
        initWidget(uiBinder.createAndBindUi(this));
        //WidgetUtils.stopClickPropagation(this);
        this.tool = canvasTool;
        this.toolPanel.add(canvasTool);


        WidgetUtils.stopClickPropagation(this.closeLink.asWidget());
        WidgetUtils.stopClickPropagation(this.moveBackLink.asWidget());
        WidgetUtils.stopClickPropagation(this.moveFrontLink.asWidget());

        NativeUtils.disableTextSelectInternal(this.buttonsPanel.getElement(), true);

        this.rotatePanel.setVisible(tool.canRotate());
        this.resizePanel.setVisible(tool.getResizeMode() != ResizeMode.NONE);
    }

    @Override
    protected void onLoad()
    {
        super.onLoad();
        this.initToolbar();
        this.reRegisterFrameHandlers();
        this.registerTransformHandlers();
    }

    @Override
    protected void onUnload()
    {
        super.onUnload();
        this.frameRegs.clear();
        this.toolRegs.clear();
        if (null != this.floatingToolbar) {
            this.floatingToolbar.setEditedWidget(null);
            this.floatingToolbar.removeFromParent();
        }
    }

    private void initToolbar()
    {
        IsWidget toolbar = this.tool.getToolbar();
        if (null == toolbar) {
            return;
        }
        this.floatingToolbar = new FloatingToolbar();
        this.floatingToolbar.setEditedWidget(this);
        this.floatingToolbar.add(toolbar);
        if (this.isAttached()) {
            RootPanel.get().add(this.floatingToolbar);
        }
    }

	private void reRegisterFrameHandlers() {
	    final CanvasToolFrameImpl that = this;

		frameRegs.clear();

		frameRegs.add(this.addAttachHandler(new AttachEvent.Handler() {
            @Override public void onAttachOrDetach(AttachEvent event) {
                if (null == that.floatingToolbar) {
                    return;
                }
                if (event.isAttached()) {
                    RootPanel.get().add(that.floatingToolbar);
                }
                else {
                    that.floatingToolbar.removeFromParent();
                }
        }}));

		frameRegs.add(this.toolPanel. addDomHandler(new KeyDownHandler(){
            @Override public void onKeyDown(KeyDownEvent event) {
                //Stop propogation of KeyDown events from the toolframe so that the worksheet
                //won't get any keydown that was already handled by the tool.
                event.stopPropagation();
        }}, KeyDownEvent.getType()));

		frameRegs.add(this.closeLink.addClickHandler(new ClickHandler() {
            @Override public void onClick(ClickEvent event) {
                closeRequest.dispatch(null);
        }}));

		frameRegs.add(this.moveBackLink.addClickHandler(new ClickHandler() {
            @Override public void onClick(ClickEvent event) {
                moveBackRequest.dispatch(null);
        }}));

		frameRegs.add(this.moveFrontLink.addClickHandler(new ClickHandler() {
            @Override public void onClick(ClickEvent event) {
                moveFrontRequest.dispatch(null);
        }}));

		frameRegs.add(this.resizePanel.addDomHandler(new MouseDownHandler() {
            @Override public void onMouseDown(MouseDownEvent event) {
                resizeStartRequest.dispatch(event);
        }}, MouseDownEvent.getType()));

		frameRegs.add(this.rotatePanel.addDomHandler(new MouseDownHandler() {
            @Override public void onMouseDown(MouseDownEvent event) {
                rotateStartRequest.dispatch(event);
        }}, MouseDownEvent.getType()));

		frameRegs.add(this.frameHeader.addDomHandler(new MouseDownHandler() {
            @Override public void onMouseDown(final MouseDownEvent event) {
                onHeaderMouseDown(event);
        }}, MouseDownEvent.getType()));

		frameRegs.add(tool.addSelfMoveRequestEventHandler(new Handler<Point2D>() {
			@Override public void onFire(Point2D offset) {
				toolSelfMoveRequest(offset);
		}}));

		frameRegs.add(this.addDomHandler(new MouseDownHandler(){
			@Override public void onMouseDown(MouseDownEvent event) {
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
        this.toolRegs.add(this.tool.addMoveStartEventHandler(new SimpleEvent.Handler<MouseEvent<?>>() {
            @Override
            public void onFire(MouseEvent<?> arg) {
                moveStartRequest.dispatch(arg);
            }
        }));
    }

    @Override
    public void setViewMode(boolean inViewMode) {
    	if (inViewMode == this._viewMode) {
    		return;
    	}
    	this._viewMode = inViewMode;
    	if (this._viewMode) {
    		frameRegs.clear();
    	}
    	else {
    		this.reRegisterFrameHandlers();
    	}
    	this.tool.setViewMode(this._viewMode);
    }

    @Override
    public CanvasTool<? extends ElementData> getTool() {
        return this.tool;
    }

    @Override
    public HandlerRegistration addCloseRequestHandler(SimpleEvent.Handler<Void> handler) {
        return this.closeRequest.addHandler(handler);
    }

    @Override
    public HandlerRegistration addMoveStartRequestHandler(SimpleEvent.Handler<MouseEvent<?>> handler) {
        return this.moveStartRequest.addHandler(handler);
    }

    @Override
    public HandlerRegistration addMoveBackRequestHandler(SimpleEvent.Handler<Void> handler) {
        return this.moveBackRequest.addHandler(handler);
    }

    @Override
    public HandlerRegistration addMoveFrontRequestHandler(SimpleEvent.Handler<Void> handler) {
        return this.moveFrontRequest.addHandler(handler);
    }

    @Override
    public HandlerRegistration addResizeStartRequestHandler(SimpleEvent.Handler<MouseEvent<?>> handler) {
        return this.resizeStartRequest.addHandler(handler);
    }

    @Override
    public HandlerRegistration addRotateStartRequestHandler(SimpleEvent.Handler<MouseEvent<?>> handler) {
        return this.rotateStartRequest.addHandler(handler);
    }

    @Override
    public HandlerRegistration addMouseDownHandler(MouseDownHandler handler)
    {
        return this.addDomHandler(handler, MouseDownEvent.getType());
    }

    @Override
    public HandlerRegistration addMouseUpHandler(MouseUpHandler handler)
    {
        return this.addDomHandler(handler, MouseUpEvent.getType());
    }

    @Override
    public Point2D getToolSize() {
        return ElementUtils.getElementClientSize(this.tool.asWidget().getElement());
    }


//    public void setToolPosition(Point2D pos)
//    {
//        Point2D toolPosition = new Point2D(
//            this.toolPanel.getAbsoluteLeft(), this.toolPanel.getAbsoluteLeft());
//        Point2D framePosition = new Point2D(
//            this.getAbsoluteLeft(), this.getAbsoluteLeft());
//        Point2D actualPosition = pos.minus(toolPosition.minus(framePosition));
//
//        ElementUtils.setElementPosition(this.getElement(), actualPosition);
//    }


    /**
     * Note: make sure the size here does NOT include padding/margin/border of the tool, otherwise
     * getToolSize and setToolSize will not be compatible (will be using different values.)
     */
    @Override
    public void setToolSize(Point2D size) {
        Element toolElement = this.tool.asWidget().getElement();
        switch (this.tool.getResizeMode())
    	{
    	    case BOTH:
    	        ElementUtils.setElementSize(toolElement, size);
    	        break;
    	    case WIDTH_ONLY:
    	        toolElement.getStyle().setWidth(size.getX(), Unit.PX);
    	        break;
    	    case HEIGHT_ONLY:
    	        toolElement.getStyle().setHeight(size.getY(), Unit.PX);
                break;
    	    case RELATIVE:
    	        int uniformSize = (size.getX() + size.getY()) / 2;
                ElementUtils.setElementSize(toolElement, new Point2D(uniformSize, uniformSize));
                break;
    	    case NONE:
    	    default:
    	        return;
    	}
        this.onResize();
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
        setFloatingToolbarVisible(focused);
		this.focusPanel.setFocus(focused);
	}

    @Override
	public void setTabIndex(int index) {
		this.focusPanel.setTabIndex(index);
	}

    @Override
	public HandlerRegistration addBlurHandler(BlurHandler handler) {
	    RegistrationsManager regs = new RegistrationsManager();
        regs.add(tool.addBlurHandler(handler));
		regs.add(this.focusPanel.addBlurHandler(handler));
		return regs.asSingleRegistration();
	}

	@Override
	public HandlerRegistration addFocusHandler(FocusHandler handler) {
        RegistrationsManager regs = new RegistrationsManager();
        regs.add(tool.addFocusHandler(handler));
        regs.add(this.focusPanel.addFocusHandler(handler));
        return regs.asSingleRegistration();
	}

	/**
	 * Notifies the CanvasToolFrame that it is being dragged / not being dragged.
	 * The dragged state is actually a stack, so that if several different mechanisms
	 * want the frame to think it's being dragged, it will prevent one of them from
	 * turning off the drag state by mistake while the frame is still being considered dragged
	 * by another mechanism.
	 * This is used for knowing whether we should pass setActive commands in to the CanvasTool.
	 * @param isDragging
	 */
    @Override
    public void setDragging(boolean isDragging)
    {
        this.draggingStackDepth += isDragging ? 1 : -1;
        this.draggingStackDepth = Math.max(this.draggingStackDepth, 0);
        if (this.draggingStackDepth > 0) {
            this.addStyleName(CanvasResources.INSTANCE.main().drag());
        }
        else {
            this.removeStyleName(CanvasResources.INSTANCE.main().drag());
            this.updateToolActive();

            // heuristic - we assume we have just finished moving.
            this.onTransformed();
        }
    }

    /**
     * Wraps CanvasTool.setActive so that if the tool frame is being dragged,
     * it will not be set active until the operation ends.
     * This is REQUIRED: because if the tool steals focus when it becomes active,
     * the drag manager in the worksheet may receive a stop event immediately.
     * @param isActive
     */
    @Override
    public void setActive(boolean isActive)
    {
        this._isActive = isActive;
        this.updateToolActive();
    }

	private void toolSelfMoveRequest(Point2D offset) {
		// TODO find a more global way of handling view mode.
		if (this._viewMode) {
			return;
		}
		Point2D newPos = ElementUtils.getElementOffsetPosition(getElement()).plus(offset);
		ElementUtils.setElementCSSPosition(getElement(), newPos);
	}

    private void updateToolActive()
    {
        if (0 >= this.draggingStackDepth) {
            this.tool.setActive(this._isActive);
            setFloatingToolbarVisible(this._isActive);
        }
    }

    private void setFloatingToolbarVisible(boolean floatingToolbarVisible)
    {
        if (null != this.floatingToolbar) {
            this.floatingToolbar.setEditedWidget(floatingToolbarVisible ? this : null);
        }
    }

    @Override
    public void onTransformed()
    {
        SchedulerUtils.OneTimeScheduler.get().scheduleDeferredOnce(new ScheduledCommand() {
            @Override public void execute() {
                handleOnTransform();
            }
        });
    }

    private void onResize()
    {
        this.onTransformed();
    }

    private void handleOnTransform()
    {
        if (null != this.floatingToolbar) {
            this.floatingToolbar.updatePosition();
        }
    }

}
