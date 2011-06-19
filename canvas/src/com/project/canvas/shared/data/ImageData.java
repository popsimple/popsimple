package com.project.canvas.shared.data;

public class ImageData extends ElementData
{
    private static final long serialVersionUID = 1L;

    public ImageInformation imageInformation = new ImageInformation();

    protected ImageData()
    {
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

        copy.imageInformation.url = this.imageInformation.url;
        copy.imageInformation.repeat = this.imageInformation.repeat;
        copy.imageInformation.center = this.imageInformation.center;
        copy.imageInformation.stretchWidth = this.imageInformation.stretchWidth;
        copy.imageInformation.stretchHeight = this.imageInformation.stretchHeight;
    }
}
