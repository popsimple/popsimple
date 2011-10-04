package com.project.website.canvas.shared.data;

import com.google.appengine.api.datastore.Blob;
import com.google.code.twig.annotation.Type;

public class SketchData extends ElementData
{
    private static final long serialVersionUID = 1L;

    public SketchData(String uniqueId)
    {
        super(uniqueId);
    }

    protected SketchData()
    {
        super();
    }

    public int penWidth;

    @Type(Blob.class)
    public String imageData;

    public int penSkip;

    public int eraserWidth;

}
