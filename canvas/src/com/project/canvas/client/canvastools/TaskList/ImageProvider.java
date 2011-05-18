package com.project.canvas.client.canvastools.TaskList;

import java.util.HashMap;

import com.google.gwt.regexp.shared.RegExp;
import com.project.canvas.client.resources.CanvasResources;

public class ImageProvider {
    private static String UNKNOWN_IMAGE_URL = "http://cdn2.iconfinder.com/data/icons/oxygen/32x32/categories/system-help.png";

    HashMap<String, String> images = new HashMap<String, String>();

    public ImageProvider() {
        this.InitializeImages();
    }

    private void InitializeImages() {
        this.images.put("robot", CanvasResources.INSTANCE.robotIcon32().getURL()); 
        this.images.put("fix", CanvasResources.INSTANCE.fixIcon32().getURL());
        this.images.put("bike", CanvasResources.INSTANCE.bicycleIcon32().getURL());
        this.images.put("bank", CanvasResources.INSTANCE.bankIcon32().getURL());
        this.images.put("phone", CanvasResources.INSTANCE.phoneIcon32().getURL());
        this.images.put("call", CanvasResources.INSTANCE.phoneIcon32().getURL());
        this.images.put("gay", CanvasResources.INSTANCE.rainbowIcon32().getURL());
    }

    public String GetDefaultImageUrl() {
        return CanvasResources.INSTANCE.taskDefaultIcon().getURL();
    }

    public String GetImageUrl(String imageTag) {
        for (String key : this.images.keySet()) {
            // TODO: Use consts for the RegExp flags.
            if (RegExp.compile(key, "i").test(imageTag)) {
                return this.images.get(key);
            }
        }
        return UNKNOWN_IMAGE_URL;
    }
}
