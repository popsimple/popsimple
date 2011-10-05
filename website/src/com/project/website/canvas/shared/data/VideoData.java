package com.project.website.canvas.shared.data;

import com.project.shared.interfaces.ICloneable;


public class VideoData extends ElementData implements ICloneable<VideoData> {
    private static final long serialVersionUID = 1L;

    public VideoInformation videoInformation = new VideoInformation();

    protected VideoData(){
        super();
    }

    public VideoData(String factoryUniqueId) {
        super(factoryUniqueId);
    }

    public VideoData(VideoData other) {
        super(other);
        this.videoInformation = new VideoInformation(other.videoInformation);
    }

    @Override
    public ICloneable<? extends ElementData> getCloneable()
    {
        return this;
    }

    @Override
    public VideoData getClone()
    {
        return new VideoData(this);
    }
}
