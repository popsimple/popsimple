package com.project.website.canvas.shared.data;

import com.google.code.twig.annotation.Embedded;
import com.project.gwtmapstraction.client.mxn.MapProvider;
import com.project.gwtmapstraction.client.mxn.MapType;
import com.project.shared.data.Location;

public class MapData extends ElementData
{
    private static final long serialVersionUID = 1L;

    protected MapData() {}

    public MapData(String factoryId) {
        super(factoryId);
    }

    @Embedded
    public Location center;

    public int zoom;
    
    // TODO: Is it sane to store a type that is defined by a third-party library (well, a wrapper thereof)?
    // If the type changes, what will happen to our serialization / storage compatibility?
    @Embedded
    public MapType mapType = MapType.ROAD;
    @Embedded
    public MapProvider provider = MapProvider.GOOGLE;
    
}
