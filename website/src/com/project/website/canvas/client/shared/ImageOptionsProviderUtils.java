package com.project.website.canvas.client.shared;

import java.util.Map.Entry;

import com.project.shared.utils.ObjectUtils;
import com.project.website.canvas.shared.data.ImageOptions;

public class ImageOptionsProviderUtils
{
    public static ImageOptionTypes getImageOptionType(
            ImageOptionsProvider optionsProvider, ImageOptions imageOptions)
    {
        for (Entry<ImageOptionTypes, ImageOptions> entry :
                optionsProvider.getImageOptionMap().entrySet())
        {
            if (ObjectUtils.areEqual(entry.getValue(), imageOptions))
            {
                return entry.getKey();
            }
        }
        return ImageOptionTypes.Custom;
    }

    public static void setImageOptions(ImageOptionsProvider optionsProvider,
            ImageOptions imageOptions, ImageOptionTypes imageOptionType)
    {
        ImageOptions knownOptions = optionsProvider.getImageOptionMap().get(imageOptionType);
        if (null == knownOptions)
        {
            //TODO: Throw exception;
            return;
        }
        knownOptions.copyTo(imageOptions);
    }

}
