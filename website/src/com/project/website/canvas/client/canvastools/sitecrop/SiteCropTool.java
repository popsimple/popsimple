package com.project.website.canvas.client.canvastools.sitecrop;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.client.handlers.SpecificKeyPressHandler;
import com.project.shared.client.utils.ElementUtils;
import com.project.shared.client.utils.NativeUtils;
import com.project.shared.client.utils.WidgetUtils;
import com.project.shared.data.Point2D;
import com.project.shared.data.Rectangle;
import com.project.shared.utils.RectangleUtils;
import com.project.website.canvas.client.canvastools.base.CanvasTool;
import com.project.website.canvas.client.resources.CanvasResources;
import com.project.website.canvas.client.worksheet.ElementDragManagerImpl;
import com.project.website.canvas.client.worksheet.interfaces.ElementDragManager.StopCondition;
import com.project.website.canvas.shared.data.ElementData;

public class SiteCropTool extends Composite implements CanvasTool<ElementData>{

    private static SiteCropToolUiBinder uiBinder = GWT.create(SiteCropToolUiBinder.class);

    interface SiteCropToolUiBinder extends UiBinder<Widget, SiteCropTool> {
    }

    @UiField
    TextBox urlTextBox;

    @UiField
    Frame siteFrame;

    @UiField
    FlowPanel frameContainer;
    @UiField
    HTMLPanel blockPanel;
    @UiField
    HTMLPanel selectionPanel;
    @UiField
    HTMLPanel dragPanel;
    @UiField
    FlowPanel coverPanel;

    @UiField
    CheckBox chkAutoSize;

    @UiField
    ToggleButton cutButton;

    @UiField
    ToggleButton browseButton;

//    ONLY FOR DEBUGGING
    @UiField
    Label urlLabel;

    private final int FRAME_STEP = 20;

    private int _frameLeft = 0;
    private int _frameTop = 0;
    private Point2D _lastPoint = Point2D.zero;
    private final SimpleEvent<Void> stopOperationEvent = new SimpleEvent<Void>();
    private ElementDragManagerImpl _frameDragManager = null;
    private SiteFrameSelectionManager _frameSelectionManager = null;

    private SimpleEvent<Point2D> _selfMoveEvent = new SimpleEvent<Point2D>();

    public SiteCropTool() {
        initWidget(uiBinder.createAndBindUi(this));

        this.addStyleName(CanvasResources.INSTANCE.main().cropSiteToolEmpty());

        this.selectionPanel.setVisible(false);
        this.dragPanel.setVisible(false);

        this.registerHandlers();

        WidgetUtils.disableContextMenu(this.blockPanel);

        this._frameDragManager = new ElementDragManagerImpl(
                this.frameContainer, this.dragPanel, 0, this.stopOperationEvent);
        this._frameSelectionManager = new SiteFrameSelectionManager(
                this.frameContainer, this.dragPanel, this.selectionPanel, this.stopOperationEvent,
                new Handler<Rectangle>(){
                    @Override
                    public void onFire(Rectangle arg) {
                        cropFrame(arg);
                    }});

//        ONLY FOR DEBUG
        urlLabel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setUrl("http://www.google.com");
            }
        });
    }

    private void cropFrame(Rectangle rect)
    {
//        this.selectionPanel.setVisible(true);
        coverPanel.getElement().getStyle().setProperty("clip",
                RectangleUtils.toRect(rect, Unit.PX));
        ElementUtils.setElementRectangle(coverPanel.getElement(),
                ElementUtils.getElementOffsetRectangle(coverPanel.getElement()));

        this._selfMoveEvent.dispatch(new Point2D(rect.getLeft(), rect.getTop()));

        ElementUtils.setElementPosition(this.coverPanel.getElement(),
                Point2D.zero.minus(new Point2D(rect.getLeft(), rect.getTop())));
        ElementUtils.setElementSize(this.getElement(), rect.getSize());
    }

    private void registerHandlers()
    {
        this.urlTextBox.addKeyPressHandler(new SpecificKeyPressHandler(KeyCodes.KEY_ENTER) {
            @Override
            public void onSpecificKeyPress(KeyPressEvent event) {
                setUrl(urlTextBox.getText());
            }
        });

        this.browseButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                blockPanel.setVisible(false == event.getValue());
            }
        });

        this.blockPanel.addDomHandler(new ContextMenuHandler() {

            @Override
            public void onContextMenu(ContextMenuEvent event) {
                event.preventDefault();
            }
        }, ContextMenuEvent.getType());

        this.blockPanel.addDomHandler(new MouseDownHandler() {

            @Override
            public void onMouseDown(MouseDownEvent event) {
                if (NativeEvent.BUTTON_LEFT != event.getNativeButton())
                {
                    return;
                }
                NativeUtils.disableTextSelectInternal(blockPanel.getElement(), true);
                Handler<Point2D> moveHandler = new Handler<Point2D>() {
                    @Override
                    public void onFire(Point2D arg) {
                        Point2D deltaPoint = arg.minus(_lastPoint);
                        _lastPoint = arg;
                        updateFrameLeft(deltaPoint.getX());
                        updateFrameTop(deltaPoint.getY());
                    }
                };

                _lastPoint = Point2D.zero;
                _frameDragManager.startMouseMoveOperation(blockPanel.getElement(),
                        ElementUtils.relativePosition(event, blockPanel.getElement()),
                        moveHandler, null, null, StopCondition.STOP_CONDITION_MOUSE_UP);

            }
        }, MouseDownEvent.getType());

        this.blockPanel.addDomHandler(new MouseDownHandler() {

            @Override
            public void onMouseDown(MouseDownEvent event) {
                if (NativeEvent.BUTTON_RIGHT != event.getNativeButton())
                {
                    return;
                }
                NativeUtils.disableTextSelectInternal(blockPanel.getElement(), true);
                _frameSelectionManager.startSelectionDrag(event);
            }
        }, MouseDownEvent.getType());
    }

    private void updateFrameTop(int delta)
    {
        this._frameTop += delta;
        Style frameStyle = siteFrame.getElement().getStyle();
        frameStyle.setTop(this._frameTop, Unit.PX);
        if (this.chkAutoSize.getValue())
        {
            frameStyle.setHeight(siteFrame.getOffsetHeight() - delta, Unit.PX);
        }
    }

    private void updateFrameLeft(int delta)
    {
        this._frameLeft += delta;
        Style frameStyle = siteFrame.getElement().getStyle();
        frameStyle.setLeft(this._frameLeft, Unit.PX);
        if (this.chkAutoSize.getValue())
        {
            frameStyle.setWidth(siteFrame.getOffsetWidth() - delta, Unit.PX);
        }
    }

//    public final native int getHorizontalScroll(Element frame) /*-{
//        return frame.Height;
//    }-*/;


    private void setUrl(String text) {
        siteFrame.setUrl(text);
        this.siteFrame.getElement().setPropertyString("scrolling", "no");
        ElementUtils.setElementSize(this.getElement(),
                ElementUtils.getElementOffsetSize(this.getElement()));
        this.removeStyleName(CanvasResources.INSTANCE.main().cropSiteToolEmpty());
        this.addStyleName(CanvasResources.INSTANCE.main().cropSiteToolSet());
    }

    @Override
    public void setValue(ElementData value) {
        // TODO Auto-generated method stub
    }

    @Override
    public ElementData getValue() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HandlerRegistration addKillRequestEventHandler(Handler<String> handler) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HandlerRegistration addMoveStartEventHandler(Handler<MouseEvent<?>> handler) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HandlerRegistration addSelfMoveRequestEventHandler(Handler<Point2D> handler) {
        return this._selfMoveEvent.addHandler(handler);
    }

    @Override
    public void setElementData(ElementData data) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setActive(boolean isActive) {
        // TODO Auto-generated method stub
    }

    @Override
    public void bind() {
        // TODO Auto-generated method stub
    }

    @Override
    public ResizeMode getResizeMode() {
        return ResizeMode.BOTH;
    }

    @Override
    public boolean canRotate() {
        return true;
    }

    @Override
    public void setViewMode(boolean isViewMode) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onResize() {
//        this.siteFrame.getElement().getStyle().setWidth(this.getElement().getOffsetWidth(), Unit.PX);
    }

}
