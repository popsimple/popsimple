package com.project.website.canvas.shared.data;

import com.google.appengine.api.datastore.Blob;
import com.google.code.twig.annotation.Type;
import com.project.shared.interfaces.ICloneable;

public class SketchData extends ElementData implements ICloneable<SketchData>
{
    private static final long serialVersionUID = 1L;

    public int penWidth;

    @Type(Blob.class)
    public String imageData;

    public int penSkip;

    public int eraserWidth;

    public SketchData(String uniqueId)
    {
        super(uniqueId);
    }

    protected SketchData()
    {
        super();
    }

    public SketchData(SketchData sketchData)
    {
        super(sketchData);
        this.penSkip = sketchData.penSkip;
        this.penWidth = sketchData.penWidth;
        this.imageData = sketchData.imageData;
        this.eraserWidth = sketchData.eraserWidth;
    }

    @Override
    public ICloneable<? extends ElementData> getCloneable()
    {
        return this;
    }

    @Override
    public SketchData getClone()
    {
        return new SketchData(this);
    }

}
