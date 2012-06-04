package com.project.website.canvas.client.canvastools.image;

import java.util.HashMap;

import com.project.website.canvas.client.shared.ImageOptionTypes;
import com.project.website.canvas.client.shared.ImageOptionsProvider;
import com.project.website.canvas.shared.data.ImageOptions;

public class ImageToolOptionsProvider implements ImageOptionsProvider
{
    private static HashMap<ImageOptionTypes, ImageOptions> _optionMap = createOptionMap();

    private static HashMap<ImageOptionTypes, ImageOptions> createOptionMap()
    {
        HashMap<ImageOptionTypes, ImageOptions> optionMap = new HashMap<ImageOptionTypes, ImageOptions>();
        optionMap.put(ImageOptionTypes.Stretch, createStretchOption());
        optionMap.put(ImageOptionTypes.Repeat, createRepeatOption());
        optionMap.put(ImageOptionTypes.OriginalSize, createOriginalSizeOption());
        return optionMap;
    }

    private static ImageOptions createStretchOption()
    {
        ImageOptions options = new ImageOptions();
        options.stretchHeight = true;
        options.stretchWidth = true;
        options.centerX = false;
        options.centerY = false;
        options.repeat = false;
        options.useOriginalSize = false;
        return options;
    }

    private static ImageOptions createRepeatOption()
    {
        ImageOptions options = new ImageOptions();
        options.stretchHeight = false;
        options.stretchWidth = false;
        options.centerX = false;
        options.centerY = false;
        options.repeat = true;
        options.useOriginalSize = false;
        return options;
    }

    private static ImageOptions createOriginalSizeOption()
    {
        ImageOptions options = new ImageOptions();
        options.stretchHeight = true;
        options.stretchWidth = true;
        options.centerX = false;
        options.centerY = false;
        options.repeat = false;
        options.useOriginalSize = true;
        return options;
    }

    @Override
    public HashMap<ImageOptionTypes, ImageOptions> getImageOptionMap()
    {
        return ImageToolOptionsProvider._optionMap;
    }

    @Override
    public ImageOptions getDefaultOptions() {
        return ImageToolOptionsProvider._optionMap.get(ImageOptionTypes.OriginalSize);
    }
}
