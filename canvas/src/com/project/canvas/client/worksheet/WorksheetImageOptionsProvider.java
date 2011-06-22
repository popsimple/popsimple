package com.project.canvas.client.worksheet;

import java.util.HashMap;

import com.project.canvas.client.shared.ImageOptionTypes;
import com.project.canvas.client.shared.ImageOptionsProvider;
import com.project.canvas.shared.data.ImageOptions;

public class WorksheetImageOptionsProvider implements ImageOptionsProvider
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
        options.center = false;
        options.repeat = false;
        options.useOriginalSize = false;
        return options;
    }

    private static ImageOptions createRepeatOption()
    {
        ImageOptions options = new ImageOptions();
        options.stretchHeight = false;
        options.stretchWidth = false;
        options.center = false;
        options.repeat = true;
        options.useOriginalSize = false;
        return options;
    }

    private static ImageOptions createOriginalSizeOption()
    {
        ImageOptions options = new ImageOptions();
        options.stretchHeight = false;
        options.stretchWidth = false;
        options.center = true;
        options.repeat = false;
        options.useOriginalSize = true;
        return options;
    }

    @Override
    public HashMap<ImageOptionTypes, ImageOptions> getImageOptionMap()
    {
        return WorksheetImageOptionsProvider._optionMap;
    }
}
