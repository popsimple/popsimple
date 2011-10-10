package com.project.website.canvas.client.shared;

import java.util.Map.Entry;

import com.google.common.base.Objects;
import com.project.website.canvas.shared.data.ImageOptions;

public class ImageOptionsProviderUtils
{
    public static ImageOptionTypes getImageOptionType(
            ImageOptionsProvider optionsProvider, ImageOptions imageOptions)
    {
        for (Entry<ImageOptionTypes, ImageOptions> entry :
                optionsProvider.getImageOptionMap().entrySet())
        {
            if (Objects.equal(entry.getValue(), imageOptions))
            {
                return entry.getKey();
            }
        }
        return ImageOptionTypes.Custom;
    }

    public static ImageOptions getImageOptions(ImageOptionsProvider optionsProvider, ImageOptionTypes imageOptionType)
    {
        return optionsProvider.getImageOptionMap().get(imageOptionType).getClone();
    }

}
