package com.project.canvas.shared.data;

import com.project.shared.utils.CloneableUtils;

public class ImageData extends ElementData
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
}
