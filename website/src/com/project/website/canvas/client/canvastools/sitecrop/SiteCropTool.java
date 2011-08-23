package com.project.website.canvas.client.canvastools.sitecrop;

import com.google.gwt.core.client.GWT;
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
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.client.handlers.SpecificKeyPressHandler;
import com.project.shared.client.utils.ElementUtils;
import com.project.shared.client.utils.NativeUtils;
import com.project.shared.client.utils.WidgetUtils;
import com.project.shared.data.Point2D;
import com.project.shared.data.Rectangle;
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
    Button leftButton;
    @UiField
    Button upButton;
    @UiField
    Button rightButton;
    @UiField
    Button downButton;

    @UiField
    FlowPanel frameContainer;
    @UiField
    HTMLPanel blockPanel;
    @UiField
    HTMLPanel selectionPanel;
    @UiField
    HTMLPanel dragPanel;

    private final int FRAME_STEP = 20;

    private int _frameLeft = 0;
    private int _frameTop = 0;
    private Point2D _lastPoint = Point2D.zero;
    private final SimpleEvent<Void> stopOperationEvent = new SimpleEvent<Void>();
    private ElementDragManagerImpl _frameDragManager = null;
    private SiteFrameSelectionManager _frameSelectionManager = null;

    public SiteCropTool() {
        initWidget(uiBinder.createAndBindUi(this));

        this.addStyleName(CanvasResources.INSTANCE.main().cropSiteToolEmpty());

        this.selectionPanel.setVisible(false);
        this.dragPanel.setVisible(false);

        this.registerHandlers();

        WidgetUtils.DisableContextMenu(this.blockPanel);

        this._frameDragManager = new ElementDragManagerImpl(
                this.frameContainer, this.dragPanel, 0, this.stopOperationEvent);
        this._frameSelectionManager = new SiteFrameSelectionManager(
                this.frameContainer, this.dragPanel, this.selectionPanel, this.stopOperationEvent,
                new Handler<Rectangle>(){
                    @Override
                    public void onFire(Rectangle arg) {
                        updateFrameLeft(-arg.getLeft());
                        updateFrameTop(-arg.getTop());
                        ElementUtils.setElementSize(getElement(), arg.getSize());
                    }});

        this.setUrl("http://www.google.com");
    }

    private void registerHandlers()
    {
        this.urlTextBox.addKeyPressHandler(new SpecificKeyPressHandler(KeyCodes.KEY_ENTER) {
            @Override
            public void onSpecificKeyPress(KeyPressEvent event) {
                setUrl(urlTextBox.getText());
            }
        });

        this.leftButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                updateFrameLeft(-FRAME_STEP);
            }
        });
        this.upButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                updateFrameTop(-FRAME_STEP);
            }
        });
        this.rightButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                updateFrameLeft(FRAME_STEP);
            }
        });
        this.downButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                updateFrameTop(FRAME_STEP);
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
        frameStyle.setHeight(siteFrame.getOffsetHeight() - delta, Unit.PX);
    }

    private void updateFrameLeft(int delta)
    {
        this._frameLeft += delta;
        Style frameStyle = siteFrame.getElement().getStyle();
        frameStyle.setLeft(this._frameLeft, Unit.PX);
        frameStyle.setWidth(siteFrame.getOffsetWidth() - delta, Unit.PX);
    }


    private void setUrl(String text) {
        siteFrame.setUrl(text);
        this.siteFrame.getElement().setPropertyString("scrolling", "no");
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
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
    }

}
