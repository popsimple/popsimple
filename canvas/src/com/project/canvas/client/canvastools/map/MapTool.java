package com.project.canvas.client.canvastools.map;

import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.user.client.ui.FlowPanel;
import com.project.canvas.client.canvastools.base.CanvasTool;
import com.project.canvas.client.resources.CanvasResources;
import com.project.canvas.client.shared.events.SimpleEvent.Handler;
import com.project.canvas.shared.ApiKeys;
import com.project.canvas.shared.data.ElementData;
import com.project.canvas.shared.data.MapData;
import com.project.canvas.shared.data.Point2D;

public class MapTool extends FlowPanel implements CanvasTool<MapData>
{
    public static void prepareApi()
    {
        prepareApi(null);
    }
    
    private static void prepareApi(final MapTool instance)
    {
        Maps.loadMapsApi(ApiKeys.GOOGLE_MAPS, "2.x", false, new Runnable() {
            @Override
            public void run()
            {
                if (null != instance) {
                    instance.onApiReady();
                }
            }
        });
    }
    
    private MapWidget mapWidget;
    private MapData mapData;

    public MapTool()
    {
        this.addStyleName(CanvasResources.INSTANCE.main().mapTool());
        this.addStyleName(CanvasResources.INSTANCE.main().mapToolEmpty());
        MapTool.prepareApi(this);
    }

    @Override
    public HandlerRegistration addKillRequestEventHandler(Handler<String> handler)
    {
        return null;
    }

    @Override
    public HandlerRegistration addMoveStartEventHandler(Handler<MouseEvent<?>> handler)
    {
        return null;
    }

    @Override
    public HandlerRegistration addSelfMoveRequestEventHandler(Handler<Point2D> handler)
    {
        return null;
    }

    @Override
    public void bind()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean canRotate()
    {
        return true;
    }

    @Override
    public ResizeMode getResizeMode()
    {
        return ResizeMode.BOTH;
    }

    @Override
    public MapData getValue()
    {
        if (null != this.mapWidget) {
            LatLng center = this.mapWidget.getCenter();
            this.mapData.center.latitude = center.getLatitude();
            this.mapData.center.longitude = center.getLongitude();
            this.mapData.zoom = this.mapWidget.getZoomLevel();
        }
        return this.mapData;
    }

    @Override
    public void setActive(boolean isActive)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void setElementData(ElementData data)
    {
        this.setValue((MapData) data);
    }

    @Override
    public void setValue(MapData value)
    {
        this.mapData = value;
        this.applyMapDataToWidget();
    }

    @Override
    public void setViewMode(boolean isViewMode)
    {
        // TODO Auto-generated method stub

    }

    private void applyMapDataToWidget()
    {
        if ((null == this.mapData) || (null == this.mapWidget)) {
            return;
        }
        this.mapWidget.setCenter(LatLng.newInstance(this.mapData.center.latitude, this.mapData.center.longitude));
        this.mapWidget.setZoomLevel(this.mapData.zoom);
    }

    private void onApiReady()
    {
        this.removeStyleName(CanvasResources.INSTANCE.main().mapToolEmpty());
        this.mapWidget = new MapWidget();
        this.add(this.mapWidget);
        this.applyMapDataToWidget();
    }

}
