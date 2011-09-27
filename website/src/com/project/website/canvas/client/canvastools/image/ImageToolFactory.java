package com.project.website.canvas.client.canvastools.image;

import com.project.website.canvas.client.canvastools.base.CanvasToolFactoryBase;
import com.project.website.canvas.client.shared.ImageOptionTypes;
import com.project.website.canvas.client.shared.ImageOptionsProviderUtils;
import com.project.website.canvas.client.shared.searchProviders.SearchProviders;
import com.project.website.canvas.shared.data.ImageData;

public class ImageToolFactory extends CanvasToolFactoryBase<ImageTool>
{
    public static final String UNIQUE_ID = "ImageToolFactory";

    private ImageToolOptionsProvider _optionsProvider = new ImageToolOptionsProvider();

    public ImageTool create()
    {
        ImageTool imageTool = new ImageTool(SearchProviders.getDefaultImageSearchProviders());
        imageTool.setValue(this.createDefaultImageData());
        return imageTool;
    }

    protected ImageData createDefaultImageData()
    {
        ImageData imageData = new ImageData(UNIQUE_ID);

        ImageOptionsProviderUtils.setImageOptions(this._optionsProvider,
                imageData.imageInformation.options, ImageOptionTypes.OriginalSize);
        return imageData;
    }

    @Override
    public String getFactoryId()
    {
        return ImageToolFactory.UNIQUE_ID;
    }
}