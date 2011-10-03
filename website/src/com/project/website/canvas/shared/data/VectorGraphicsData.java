package com.project.website.canvas.shared.data;

import com.google.appengine.api.datastore.Blob;
import com.google.code.twig.annotation.Type;

public class VectorGraphicsData extends ElementData
{
    private static final long serialVersionUID = 1L;

    public VectorGraphicsData(String uniqueId)
    {
        super(uniqueId);
    }

    protected VectorGraphicsData()
    {
        super();
    }

    public int penWidth;

    @Type(Blob.class)
    public String svgString;

    public int penSkip;

    public int eraserWidth;

}
