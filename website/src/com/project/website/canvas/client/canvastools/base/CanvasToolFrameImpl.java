package com.project.website.canvas.client.canvastools.base;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HumanInputEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
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
import com.project.shared.client.utils.DocumentUtils;
import com.project.shared.client.utils.ElementUtils;
import com.project.shared.client.utils.SchedulerUtils;
import com.project.shared.client.utils.StyleUtils;
import com.project.shared.client.utils.widgets.WidgetUtils;
import com.project.shared.data.Point2D;
import com.project.shared.data.Rectangle;
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

    @UiField
    HTMLPanel loadingPanel;

    protected final CanvasTool<?> tool;

    private FloatingToolbar floatingToolbar = null;

    protected final SimpleEvent<Void> closeRequest = new SimpleEvent<Void>();
    protected final SimpleEvent<Void> moveBackRequest = new SimpleEvent<Void>();
    protected final SimpleEvent<Void> moveFrontRequest = new SimpleEvent<Void>();
    protected final SimpleEvent<Void> moveStartRequest = new SimpleEvent<Void>();
    protected final SimpleEvent<Void> resizeStartRequest = new SimpleEvent<Void>();
    protected final SimpleEvent<Void> rotateStartRequest = new SimpleEvent<Void>();

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

        ElementUtils.setTextSelectionEnabled(this.buttonsPanel.getElement(), false);

        if (this.tool.dimOnLoad())
        {
            this.loadingPanel.addStyleName(CanvasResources.INSTANCE.main().loadingFillerDim());
        }
        this.loadingPanel.setVisible(false);
        this.rotatePanel.setVisible(tool.canRotate());
        this.resizePanel.setVisible(tool.getResizeMode() != ResizeMode.NONE);
        this.preventTouchScroll();
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

		frameRegs.add(this.toolPanel.addDomHandler(new KeyDownHandler(){
            @Override public void onKeyDown(KeyDownEvent event) {
                //Stop propogation of KeyDown events from the toolframe so that the worksheet
                //won't get any keydown that was already handled by the tool.
                event.stopPropagation();
        }}, KeyDownEvent.getType()));

		frameRegs.add(this.closeLink.addClickHandler(new ClickHandler() {
            @Override public void onClick(ClickEvent event) {
                that.closeRequest.dispatch(null);
        }}));

		frameRegs.add(this.moveBackLink.addClickHandler(new ClickHandler() {
            @Override public void onClick(ClickEvent event) {
                that.moveBackRequest.dispatch(null);
        }}));

		frameRegs.add(this.moveFrontLink.addClickHandler(new ClickHandler() {
            @Override public void onClick(ClickEvent event) {
                that.moveFrontRequest.dispatch(null);
        }}));

		frameRegs.add(WidgetUtils.addMovementStartHandler(this.resizePanel, new SimpleEvent.Handler<HumanInputEvent<?>>() {
            @Override public void onFire(HumanInputEvent<?> arg) {
                that.resizeStartRequest.dispatch(null);
            }}));

        frameRegs.add(WidgetUtils.addMovementStartHandler(this.rotatePanel, new SimpleEvent.Handler<HumanInputEvent<?>>() {
            @Override public void onFire(HumanInputEvent<?> arg) {
                that.rotateStartRequest.dispatch(null);
            }}));

        frameRegs.add(WidgetUtils.addMovementStartHandler(this.frameHeader, new SimpleEvent.Handler<HumanInputEvent<?>>() {
            @Override public void onFire(HumanInputEvent<?> arg) {
                moveStartRequest.dispatch(null);
        }}));

		frameRegs.add(tool.getToolEvents().addSelfMoveRequestEventHandler(new Handler<Point2D>() {
			@Override public void onFire(Point2D offset) {
			    that.toolSelfMoveRequest(offset);
		}}));

        frameRegs.add(WidgetUtils.addMovementStartHandler(this, new SimpleEvent.Handler<HumanInputEvent<?>>() {
            @Override public void onFire(HumanInputEvent<?> arg) {
                that.onToolFrameSelected();
            }}));
	}

	private void onToolFrameSelected()
	{
	    if (false == DocumentUtils.isActiveElementTree(this.getElement())) {
	        focusPanel.setFocus(true); // take away focus from any others
	    }
	}

    protected void registerTransformHandlers() {
        this.toolRegs.add(this.tool.getToolEvents().addMoveStartEventHandler(
                new SimpleEvent.Handler<MouseEvent<?>>() {
            @Override
            public void onFire(MouseEvent<?> arg) {
                moveStartRequest.dispatch(null);
            }
        }));
        toolRegs.add(tool.getToolEvents().addLoadStartedEventHandler(new Handler<Void>() {
            @Override
            public void onFire(Void arg) {
                toolLoadStarted();
            }
        }));

        toolRegs.add(tool.getToolEvents().addLoadEndedEventHandler(new Handler<Void>() {
            @Override
            public void onFire(Void arg) {
                toolLoadEnded();
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
    		this.setFloatingToolbarVisible(false);
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
    public HandlerRegistration addMoveStartRequestHandler(SimpleEvent.Handler<Void> handler) {
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
    public HandlerRegistration addResizeStartRequestHandler(SimpleEvent.Handler<Void> handler) {
        return this.resizeStartRequest.addHandler(handler);
    }

    @Override
    public HandlerRegistration addRotateStartRequestHandler(SimpleEvent.Handler<Void> handler) {
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
        Element toolElement = this.tool.asWidget().getElement();
        Style toolStyle = toolElement.getStyle();
        Integer width = StyleUtils.getWidthPx(toolStyle);
        Integer height = StyleUtils.getHeightPx(toolStyle);
        // Client size is not what we want, but we use it as a fallback (it includes padding, we want without)
        Point2D size = ElementUtils.getElementClientSize(toolElement);
        if (null != width) {
            size.setX(width);
        }
        if (null != height) {
            size.setY(height);
        }
        return size;
    }

    @Override
    public Point2D getToolOffsetInFrame()
    {
        Element toolPanelElement = this.toolPanel.getElement();
        Point2D offset = ElementUtils.getElementOffsetPosition(toolPanelElement);
        Rectangle paddingRect = ElementUtils.tryGetPaddingRectangle(toolPanelElement);
        if (null != paddingRect) {
            offset = offset.plus(paddingRect.getCorners().topLeft);
        }
        return offset;
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
    public Point2D setToolSize(Point2D size) {
        Element toolElement = this.tool.asWidget().getElement();
        Point2D newSize = Point2D.zero;

        switch (this.tool.getResizeMode())
    	{
    	    case BOTH:
    	        newSize = size;
    	        break;
    	    case WIDTH_ONLY:
    	        newSize = new Point2D(size.getX(), this.getToolSize().getY());
    	        break;
    	    case HEIGHT_ONLY:
    	        newSize = new Point2D(this.getToolSize().getX(), size.getY());
                break;
    	    case RELATIVE:
    	        int uniformSize = (size.getX() + size.getY()) / 2;
    	        newSize = new Point2D(uniformSize, uniformSize);
                break;
    	    case NONE:
    	    default:
    	        return this.getToolSize();
    	}
        ElementUtils.setElementSize(toolElement, newSize);
        this.onResize();

        return newSize;
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
        regs.add(tool.getToolEvents().addBlurHandler(handler));
		regs.add(this.focusPanel.addBlurHandler(handler));
		return regs.asSingleRegistration();
	}

	@Override
	public HandlerRegistration addFocusHandler(FocusHandler handler) {
        RegistrationsManager regs = new RegistrationsManager();
        regs.add(tool.getToolEvents().addFocusHandler(handler));
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

    private void toolLoadStarted()
    {
        this.loadingPanel.setVisible(true);
    }

    private void toolLoadEnded()
    {
        this.loadingPanel.setVisible(false);
    }

	private void toolSelfMoveRequest(Point2D offset) {
		// TODO find a more global way of handling view mode.
		if (this._viewMode) {
			return;
		}
		Point2D newPos = ElementUtils.getElementOffsetPosition(getElement()).plus(offset);
		ElementUtils.setElementCSSPosition(getElement(), newPos);
		this.onTransformed();
	}

    private void updateToolActive()
    {
        if (0 >= this.draggingStackDepth) {
            this.tool.setActive(this._isActive);
            setFloatingToolbarVisible(this._isActive);
            if (this._isActive) {
                this.addStyleName(CanvasResources.INSTANCE.main().activeToolFrame());
            }
            else {
                this.removeStyleName(CanvasResources.INSTANCE.main().activeToolFrame());
            }
        }
    }

    private void setFloatingToolbarVisible(boolean floatingToolbarVisible)
    {
        if (null != this.floatingToolbar) {
        	floatingToolbarVisible &= (false == this._viewMode);
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
        this.tool.onResize();
    }

    private void handleOnTransform()
    {
        if (null != this.floatingToolbar) {
            this.floatingToolbar.updatePosition();
        }
    }

    private void preventTouchScroll()
    {
        Widget widget = this;
        widget.addDomHandler(new TouchStartHandler(){
            @Override public void onTouchStart(TouchStartEvent event) {
                event.preventDefault();
            }}, TouchStartEvent.getType());
        widget.addDomHandler(new TouchEndHandler(){
            @Override public void onTouchEnd(TouchEndEvent event) {
                event.preventDefault();
            }}, TouchEndEvent.getType());
        widget.addDomHandler(new TouchMoveHandler(){
            @Override public void onTouchMove(TouchMoveEvent event) {
                event.preventDefault();
            }}, TouchMoveEvent.getType());
    }
}
