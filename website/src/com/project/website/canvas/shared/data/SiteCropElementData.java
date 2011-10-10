package com.project.website.canvas.shared.data;

import com.project.shared.data.Rectangle;
import com.project.shared.interfaces.ICloneable;

public class SiteCropElementData extends ElementData implements ICloneable<SiteCropElementData> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public String url = "";
    public Rectangle frameRectangle = new Rectangle();
    public boolean isInteractive = false;

    protected SiteCropElementData() {
        super();
    }

    public SiteCropElementData(String factoryUniqueId) {
        super(factoryUniqueId);
    }

    public SiteCropElementData(SiteCropElementData siteCropElementData)
    {
        super(siteCropElementData);
        this.url = siteCropElementData.url;
        this.frameRectangle = new Rectangle(siteCropElementData.frameRectangle);
        this.isInteractive = siteCropElementData.isInteractive;
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
