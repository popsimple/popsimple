package com.project.website.canvas.shared.data;

import com.google.code.twig.annotation.Embedded;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.project.shared.data.Location;

public class MapData extends ElementData
{
    private static final long serialVersionUID = 1L;


    public enum MapType implements IsSerializable {
        ROAD,
        SATELLITE,
        HYBRID,
        PHYSICAL;
    }

    protected MapData() {}

    public MapData(String factoryId) {
        super(factoryId);
    }

    @Embedded
    public Location center;

    public int zoom;

    public MapType mapType = MapType.ROAD;

    public String provider = null;

}
