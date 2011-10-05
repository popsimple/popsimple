package com.project.website.canvas.shared.data;

import com.project.shared.interfaces.ICloneable;


public class ImageData extends ElementData implements ICloneable<ImageData>
{
    private static final long serialVersionUID = 1L;

    public ImageInformation imageInformation = new ImageInformation();

    protected ImageData(){
    }

    public ImageData(String factoryUniqueId) {
        super(factoryUniqueId);
    }

    @Override
    public ElementData createInstance() {
        return new ImageData();
    }

    @Override
    public void copyTo(Object object) {
        super.copyTo(object);

        ImageData copy = (ImageData)object;

        copy.imageInformation = (ImageInformation)CloneableUtils.clone(this.imageInformation);
    }

    @Override
    public ICloneable<ElementData> getCloneable()
    {
        return this;
    }

    @Override
    public ImageData getClone()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
