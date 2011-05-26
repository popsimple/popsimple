package com.project.canvas.client.canvastools.map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.project.canvas.client.canvastools.base.CanvasTool;
import com.project.canvas.client.resources.CanvasResources;
import com.project.canvas.client.shared.RegistrationsManager;
import com.project.canvas.client.shared.events.SimpleEvent.Handler;
import com.project.canvas.shared.ApiKeys;
import com.project.canvas.shared.data.ElementData;
import com.project.canvas.shared.data.MapData;
import com.project.canvas.shared.data.Point2D;
import com.project.gwtmapstraction.client.mxn.MapProvider;
import com.project.gwtmapstraction.client.mxn.Mapstraction;

public class MapTool extends Composite implements CanvasTool<MapData> {

    private static final String SHOWHIDE_LABEL_HIDE = "Hide search bar";
    private static final String SHOWHIDE_LABEL_SHOW = "Show search bar";

    private static MapToolUiBinder uiBinder = GWT.create(MapToolUiBinder.class);

    interface MapToolUiBinder extends UiBinder<Widget, MapTool> {}

    public static void prepareApi() {
        prepareApi(null);
    }

    private static void prepareApi(final MapTool instance) {
        Maps.loadMapsApi(ApiKeys.GOOGLE_MAPS, "2.x", false, new Runnable() {
            @Override
            public void run() {
                if (null != instance) {
                    instance.onApiReady();
                }
            }
        });
    }

    @UiField
    FlowPanel mainPanel;
    @UiField
    FlowPanel mapPanel;
    @UiField
    Label showHideBarLabel;

    private final RegistrationsManager registrationsManager = new RegistrationsManager();
    private Widget mapWidget;
    private Mapstraction mapstraction;
    private MapData mapData;
    private boolean barEnabled;

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
        this.registrationsManager.add(this.showHideBarLabel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                toggleBarVisible();
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
            //LatLng center = this.mapWidget.getCenter();
//            this.mapData.center.latitude = center.getLatitude();
//            this.mapData.center.longitude = center.getLongitude();
//            this.mapData.zoom = this.mapWidget.getZoomLevel();
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
        if ((null == this.mapWidget) || (null == this.mapData.center)) {
            this.addStyleName(CanvasResources.INSTANCE.main().mapToolEmpty());
            return;
        }
        this.removeStyleName(CanvasResources.INSTANCE.main().mapToolEmpty());
//        this.mapWidget.setCenter(LatLng.newInstance(
//                this.mapData.center.latitude, this.mapData.center.longitude));
//        this.mapWidget.setZoomLevel(this.mapData.zoom);
    }

    private void onApiReady() {
        //this.mapWidget = new MapWidget();
        //this.setEnableOptionsBar(true);
        //this.mapWidget.setScrollWheelZoomEnabled(true);
        //this.mapWidget.setDraggable(true);
        this.mapWidget = new FlowPanel();
        this.mapstraction = new Mapstraction(this.mapWidget.getElement(), MapProvider.GOOGLE, false);
        this.mapWidget.addStyleName(CanvasResources.INSTANCE.main().mapToolMapWidget());
        this.mapPanel.add(this.mapWidget);
        this.applyMapDataToWidget();
    }

    private void setEnableOptionsBar(boolean enabled) {
        this.barEnabled = enabled;
//        this.mapWidget.setGoogleBarEnabled(enabled);
        String labelText = this.barEnabled ? SHOWHIDE_LABEL_HIDE : SHOWHIDE_LABEL_SHOW;
        showHideBarLabel.setText(labelText);
    }

    private void toggleBarVisible() {
        this.setEnableOptionsBar(false == this.barEnabled);
    }

}
