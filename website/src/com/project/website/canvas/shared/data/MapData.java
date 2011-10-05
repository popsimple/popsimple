package com.project.website.canvas.shared.data;

import com.google.code.twig.annotation.Embedded;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.project.shared.data.Location;
import com.project.shared.interfaces.ICloneable;

public class
MapData extends ElementData implements ICloneable<MapData>
{
    private static final long serialVersionUID = 1L;


    public enum MapType implements IsSerializable {
        ROAD,
        SATELLITE,
        HYBRID,
        PHYSICAL;
    }

    @Embedded
    public Location center;

    public int zoom;

    public MapType mapType = MapType.ROAD;

    public String provider = null;


    protected MapData() {
        super();
    }

    public MapData(String factoryId) {
        super(factoryId);
    }

    public MapData(MapData mapData)
    {
        super(mapData);
        this.center = new Location(mapData.center);
        this.zoom = mapData.zoom;
        this.mapType = mapData.mapType;
        this.provider = mapData.provider;
    }


    @Override
    public ICloneable<? extends ElementData> getCloneable()
    {
        return this;
    }

    @Override
    public MapData getClone()
    {
        return new MapData(this);
    }

}
