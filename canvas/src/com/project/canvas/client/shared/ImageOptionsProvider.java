package com.project.canvas.client.shared;

import java.util.HashMap;

import com.project.canvas.shared.data.ImageOptions;

public interface ImageOptionsProvider {
    HashMap<ImageOptionTypes, ImageOptions> getImageOptionMap();
}
