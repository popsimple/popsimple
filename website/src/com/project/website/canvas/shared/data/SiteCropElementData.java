package com.project.website.canvas.shared.data;

import com.project.shared.data.Rectangle;
import com.project.shared.interfaces.ICloneable;

//TODO: Move to data.
public class SiteCropElementData extends ElementData implements ICloneable<SiteCropElementData>
{
    private static final long serialVersionUID = 1L;

    public String url = "";
    public Rectangle clipRectangle = new Rectangle();
    public Rectangle coverRectangle = new Rectangle();
    public Rectangle frameRectangle = new Rectangle();
    public boolean isInteractive = false;

    protected SiteCropElementData() {
        super();
    }

    public SiteCropElementData(String factoryUniqueId) {
        super(factoryUniqueId);
    }

    public SiteCropElementData(SiteCropElementData other)
    {
        super(other);
        this.url = other.url;
        this.clipRectangle = other.clipRectangle.getClone();
        this.coverRectangle = other.coverRectangle.getClone();
        this.frameRectangle = other.frameRectangle.getClone();
        this.isInteractive = other.isInteractive;
    }

    @Override
    public ICloneable<? extends ElementData> getCloneable()
    {
        return this;
    }

    @Override
    public SiteCropElementData getClone()
    {
        return new SiteCropElementData(this);
    }

}
