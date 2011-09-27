package com.project.website.canvas.client.canvastools.sitecrop;

import com.project.shared.data.Point2D;
import com.project.shared.data.Rectangle;
import com.project.shared.utils.CloneableUtils;
import com.project.website.canvas.shared.data.ElementData;

//TODO: Move to data.
public class SiteCropElementData extends ElementData {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public String url = "";
    public Rectangle clipRectangle = new Rectangle();
    public Rectangle coverRectangle = new Rectangle();
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
        copy.clipRectangle = (Rectangle)CloneableUtils.clone(this.clipRectangle);
        copy.coverRectangle = (Rectangle)CloneableUtils.clone(this.coverRectangle);
        copy.frameRectangle = (Rectangle)CloneableUtils.clone(this.frameRectangle);
    }

}
