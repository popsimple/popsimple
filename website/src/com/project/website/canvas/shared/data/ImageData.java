package com.project.website.canvas.shared.data;

import com.project.shared.interfaces.ICloneable;


public class ImageData extends ElementData implements ICloneable<ImageData>
{
    private static final long serialVersionUID = 1L;

    public ImageInformation imageInformation = new ImageInformation();

    protected ImageData() {
        super();
    }

    public ImageData(String factoryUniqueId) {
        super(factoryUniqueId);
    }

    public ImageData(ImageData imageData)
    {
        super(imageData);
        this.imageInformation = imageData.imageInformation.getClone();
    }

    @Override
    public ImageData getClone()
    {
        return new ImageData(this);
    }

    @Override
    public ICloneable<? extends ElementData> getCloneable()
    {
        return this;
    }
}
