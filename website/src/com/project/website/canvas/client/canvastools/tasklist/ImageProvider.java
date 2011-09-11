package com.project.website.canvas.client.canvastools.tasklist;

import java.util.HashMap;

import com.google.gwt.regexp.shared.RegExp;
import com.project.website.canvas.client.resources.CanvasResources;

public class ImageProvider {
    private static String UNKNOWN_IMAGE_URL = "http://cdn2.iconfinder.com/data/icons/oxygen/32x32/categories/system-help.png";

    HashMap<String, String> images = new HashMap<String, String>();

    public ImageProvider() {
        this.initializeImages();
    }

    private void initializeImages() {
        this.images.put("robot", CanvasResources.INSTANCE.robotIcon32().getSafeUri().asString());
        this.images.put("fix", CanvasResources.INSTANCE.fixIcon32().getSafeUri().asString());
        this.images.put("bike", CanvasResources.INSTANCE.bicycleIcon32().getSafeUri().asString());
        this.images.put("bank", CanvasResources.INSTANCE.bankIcon32().getSafeUri().asString());
        this.images.put("phone", CanvasResources.INSTANCE.phoneIcon32().getSafeUri().asString());
        this.images.put("call", CanvasResources.INSTANCE.phoneIcon32().getSafeUri().asString());
        this.images.put("gay", CanvasResources.INSTANCE.rainbowIcon32().getSafeUri().asString());
    }

    public String getDefaultImageUrl() {
        return CanvasResources.INSTANCE.taskDefaultIcon().getSafeUri().asString();
    }

    public String getImageUrl(String imageTag) {
        for (String key : this.images.keySet()) {
            // TODO: Use consts for the RegExp flags.
            if (RegExp.compile(key, "i").test(imageTag)) {
                return this.images.get(key);
            }
        }
        return UNKNOWN_IMAGE_URL;
    }
}
