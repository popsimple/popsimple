package com.project.website.canvas.shared.data;

import com.project.shared.data.Rectangle;
import com.project.shared.utils.CloneableUtils;

public class SiteCropElementData extends ElementData {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public String url = "";
    public Rectangle frameRectangle = new Rectangle();
    public boolean isInteractive = false;

    protected SiteCropElementData(){
    }

    public SiteCropElementData(String factoryUniqueId) {
        super(factoryUniqueId);
    }

    @Override
    public ElementData createInstance() {
        return new SiteCropElementData();
    }

    @Override
    public void copyTo(Object object) {
        super.copyTo(object);

        SiteCropElementData copy = (SiteCropElementData)object;
        copy.url = this.url;
        copy.isInteractive = this.isInteractive;
        copy.frameRectangle = (Rectangle)CloneableUtils.clone(this.frameRectangle);
    }

}
