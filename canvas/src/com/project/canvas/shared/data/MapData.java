package com.project.canvas.shared.data;

import com.google.code.twig.annotation.Embedded;

public class MapData extends ElementData 
{
    private static final long serialVersionUID = 1L;

    @Embedded
    public Location center;

    public int zoom;
}