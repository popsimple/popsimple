package com.project.website.canvas.client.shared;

import java.util.HashMap;

import com.project.website.canvas.shared.data.ImageOptions;

public interface ImageOptionsProvider {
    HashMap<ImageOptionTypes, ImageOptions> getImageOptionMap();

    ImageOptions getDefaultOptions();
}
