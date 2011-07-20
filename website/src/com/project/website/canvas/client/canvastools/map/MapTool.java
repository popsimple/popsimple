package com.project.website.canvas.client.canvastools.map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.logical.shared.AttachEvent;
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
import com.project.gwtmapstraction.client.mxn.MapstractionMapType;
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
import com.project.website.canvas.shared.data.MapData.MapType;

public class MapTool extends Composite implements CanvasTool<MapData>
{
    interface MapToolUiBinder extends UiBinder<Widget, MapTool> {}

    private static MapToolUiBinder uiBinder = GWT.create(MapToolUiBinder.class);

    public static void prepareApi() {
        prepareApi(null);
    }

    private static void prepareApi(final MapTool instance) {
        if (null != instance) {
            instance.onApiReady();
        }
    }

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
        MapTool.prepareApi(this);
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
        if (null != this.mapWidget) {
            LatLonPoint center = this.mapstraction.getCenter();
            this.mapData.center = new Location();
            this.mapData.center.latitude = center.getLat();
            this.mapData.center.longitude = center.getLon();
            this.mapData.zoom = this.mapstraction.getZoom();
            this.mapData.mapType = MapTool.fromMapstractionMapType(this.mapstraction.getMapType());
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
        // TODO Decide how map tool should behave in view mode
    }

    private void applyMapDataToWidget() {
        if (false == this.isAttached()) {
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
        this.mapstraction.setMapType(MapTool.fromMapType(this.mapData.mapType));
        this.mapstraction.swap(MapProvider.valueOf(this.mapData.provider), this.mapWidget.getElement());
    }

    private static MapstractionMapType fromMapType(MapType mapType)
    {
        switch (mapType) {
            case HYBRID:
                return MapstractionMapType.HYBRID;
            case PHYSICAL:
                return MapstractionMapType.PHYSICAL;
            case ROAD:
                return MapstractionMapType.ROAD;
            case SATELLITE:
                return MapstractionMapType.SATELLITE;
            default:
                throw new RuntimeException("Unsupported map type: " + mapType);
        }
    }

    private static MapType fromMapstractionMapType(MapstractionMapType mapstractionMapType)
    {
        switch (mapstractionMapType) {
            case HYBRID:
                return MapType.HYBRID;
            case PHYSICAL:
                return MapType.PHYSICAL;
            case ROAD:
                return MapType.ROAD;
            case SATELLITE:
                return MapType.SATELLITE;
            default:
                throw new RuntimeException("Unsupported mapstraction map type: " + mapstractionMapType);
        }
    }

    private void updateMapSize()
    {
        Point2D widgetSize = ElementUtils.getElementClientSize(this.mapWidget.getElement());
        this.mapstraction.resizeTo(widgetSize.getX(), widgetSize.getY());
    }

    private void onApiReady() {
        this.mapWidget = new FlowPanel();
        final MapTool that = this;
        this.mapWidget.addAttachHandler(new AttachEvent.Handler() {
            @Override
            public void onAttachOrDetach(AttachEvent event) {
                if (false == event.isAttached()) {
                    return;
                }
                that.mapstraction = Mapstraction.createInstance(that.mapWidget.getElement(), MapProvider.GOOGLE_V3, false);
                //that.mapstraction.setDebug(true);
                that.mapstraction.addSmallControls();
                that.mapstraction.enableScrollWheelZoom();

                that.mapWidget.addStyleName(CanvasResources.INSTANCE.main().mapToolMapWidget());
                that.applyMapDataToWidget();
            }
        });
        that.mapPanel.add(that.mapWidget);
    }

    protected void showOptions()
    {
        if (null == this.optionsDialog) {
            this.optionsDialog = new DialogWithZIndex(false, true);
        }
        if (null == this.mapToolOptionsWidget) {
            this.mapToolOptionsWidget = new MapToolOptions();
            this.mapToolOptionsWidget.addDoneHandler(new Handler<Void>() {
                @Override
                public void onFire(Void arg)
                {
                    optionsDialog.hide();
                    setValue(mapToolOptionsWidget.getValue());
                }
            });
            this.optionsDialog.add(this.mapToolOptionsWidget);
        }
        this.mapToolOptionsWidget.setValue(this.getValue());
        this.optionsDialog.center();
    }

    @Override
    public void onResize() {
        this.updateMapSize();
    }
}
