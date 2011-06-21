package com.project.canvas.shared.data;

import com.project.canvas.shared.CloneableUtils;

public class VideoData extends ElementData {
    private static final long serialVersionUID = 1L;

    public VideoInformation videoInformation = new VideoInformation();

    protected VideoData(){
    }

    public VideoData(String factoryUniqueId) {
        super(factoryUniqueId);
    }

    @Override
    public ElementData createInstance() {
        return new VideoData();
    }

    @Override
    public void copyTo(Object object) {
        super.copyTo(object);

        VideoData copy = (VideoData)object;
        copy.videoInformation = (VideoInformation)CloneableUtils.clone(this.videoInformation);
    }
}
