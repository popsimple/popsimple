package com.project.website.canvas.client.canvastools.map;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.project.gwtmapstraction.client.mxn.LatLonPoint;
import com.project.gwtmapstraction.client.mxn.MapProvider;
import com.project.gwtmapstraction.client.mxn.Mapstraction;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.client.handlers.RegistrationsManager;
import com.project.shared.client.utils.ElementUtils;
import com.project.shared.data.Location;
import com.project.shared.data.Point2D;
import com.project.website.canvas.client.canvastools.base.CanvasTool;
import com.project.website.canvas.client.resources.CanvasResources;
import com.project.website.canvas.client.shared.widgets.DialogWithZIndex;
import com.project.website.canvas.shared.data.ElementData;
import com.project.website.canvas.shared.data.MapData;

public class MapTool extends Composite implements CanvasTool<MapData>
{
    private static final MapProvider DEFAULT_MAP_PROVIDER = MapProvider.GOOGLE_V3;

    interface MapToolUiBinder extends UiBinder<Widget, MapTool> {}

    private static MapToolUiBinder uiBinder = GWT.create(MapToolUiBinder.class);

    @UiField
    FlowPanel mainPanel;
    @UiField
    FlowPanel mapPanel;
    @UiField
    Label optionsLabel;

    private final RegistrationsManager registrationsManager = new RegistrationsManager();
    private DialogBox optionsDialog;
    private MapToolOptions mapToolOptionsWidget;
    private Widget mapWidget;
    private Mapstraction mapstraction;
    private MapData mapData;

    public MapTool() {
        initWidget(uiBinder.createAndBindUi(this));
        this.addStyleName(CanvasResources.INSTANCE.main().mapTool());
        this.addStyleName(CanvasResources.INSTANCE.main().mapToolEmpty());

        Handler<Void> loadHandler = new Handler<Void>() {
            @Override
            public void onFire(Void arg)
            {
                initializeMapWidget();
            }
        };
        MapToolStaticUtils.loadMapScripts(loadHandler);
    }



    @Override
    public HandlerRegistration addKillRequestEventHandler(
            Handler<String> handler) {
        return null;
    }

    @Override
    public HandlerRegistration addMoveStartEventHandler(
            Handler<MouseEvent<?>> handler) {
        return null;
    }

    @Override
    public HandlerRegistration addSelfMoveRequestEventHandler(
            Handler<Point2D> handler) {
        return null;
    }

    @Override
    public void bind() {
        this.registrationsManager.add(this.optionsLabel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                showOptions();
            }
        }));
    }

    @Override
    public boolean canRotate() {
        return true;
    }

    @Override
    public ResizeMode getResizeMode() {
        return ResizeMode.BOTH;
    }

    @Override
    public MapData getValue() {
        if (this.isReady()) {
            LatLonPoint center = this.mapstraction.getCenter();
            this.mapData.center = new Location();
            this.mapData.center.latitude = center.getLat();
            this.mapData.center.longitude = center.getLon();
            this.mapData.zoom = this.mapstraction.getZoom();
            this.mapData.mapType = MapToolStaticUtils.fromMapstractionMapType(this.mapstraction.getMapType());
        }
        return this.mapData;
    }

    @Override
    public void setActive(boolean isActive) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setElementData(ElementData data) {
        this.setValue((MapData) data);
    }

    @Override
    public void setValue(MapData value) {
        this.mapData = value;
        this.applyMapDataToWidget();
    }

    @Override
    public void setViewMode(boolean isViewMode) {
        this.optionsLabel.setVisible(false == isViewMode);
    }

    private void applyMapDataToWidget() {
        if (false == this.isReady()) {
            return;
        }
        if ((null == this.mapWidget) || (null == this.mapData.center)) {
            this.addStyleName(CanvasResources.INSTANCE.main().mapToolEmpty());
            this.mapstraction.setCenter(LatLonPoint.create(75.67219739055291,-130.078125));
            this.mapstraction.setZoom(1);
            updateMapSize();
            return;
        }
        this.removeStyleName(CanvasResources.INSTANCE.main().mapToolEmpty());
        this.mapstraction.setCenter(
                LatLonPoint.create(this.mapData.center.latitude, this.mapData.center.longitude));
        this.mapstraction.setZoom(this.mapData.zoom);
        this.mapstraction.setMapType(MapToolStaticUtils.fromMapType(this.mapData.mapType));
        this.mapstraction.swap(MapProvider.valueOf(mapData.provider), mapWidget.getElement());
    }



    private boolean isReady()
    {
        return (null != this.mapWidget) && this.isAttached();
    }

    private void updateMapSize()
    {
        Point2D widgetSize = ElementUtils.getElementClientSize(this.mapWidget.getElement());
        this.mapstraction.resizeTo(widgetSize.getX(), widgetSize.getY());
    }

    private void initializeMapWidget() {
        this.mapWidget = new FlowPanel();
        final MapTool that = this;
        this.mapWidget.addAttachHandler(new AttachEvent.Handler() {
            @Override
            public void onAttachOrDetach(AttachEvent event) {
                if (false == event.isAttached()) {
                    return;
                }
                initializeMapWidgetActual(DEFAULT_MAP_PROVIDER);
            }
        });
        that.mapPanel.add(that.mapWidget);

    }

    protected void showOptions()
    {
        if (null == this.optionsDialog) {
            this.optionsDialog = new DialogWithZIndex(false, true);
            this.optionsDialog.setText("Map options");
        }
        if (null == this.mapToolOptionsWidget) {
            this.mapToolOptionsWidget = new MapToolOptions(MapToolStaticUtils.AVAILABLE_PROVIDERS);
            this.mapToolOptionsWidget.addDoneHandler(new Handler<Void>() {
                @Override
                public void onFire(Void arg)
                {
                    optionsDialog.hide();
                    setValue(mapToolOptionsWidget.getValue());
                }
            });
            this.optionsDialog.add(this.mapToolOptionsWidget);
            this.mapToolOptionsWidget.addValueChangeHandler(new ValueChangeHandler<MapData>() {
                @Override
                public void onValueChange(ValueChangeEvent<MapData> event)
                {
                    setValue(event.getValue());
                }
            });
        }
        this.mapToolOptionsWidget.setValue(this.getValue());
        this.optionsDialog.center();
    }

    @Override
    public void onResize() {
        // widget may be null if api not loaded yet
        if (this.isReady()) {
            this.updateMapSize();
        }
    }



    private void initializeMapWidgetActual(final MapProvider provider)
    {
        this.mapstraction = Mapstraction.createInstance(this.mapWidget.getElement(), provider, false);
        //this.mapstraction.setDebug(true);
        this.mapstraction.addSmallControls();
        this.mapstraction.enableScrollWheelZoom();

        this.mapWidget.addStyleName(CanvasResources.INSTANCE.main().mapToolMapWidget());
        this.applyMapDataToWidget();
    }

}
