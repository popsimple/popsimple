package com.project.website.canvas.client.canvastools.video;

import com.project.website.canvas.client.canvastools.base.CanvasToolFactoryBase;
import com.project.website.canvas.client.shared.searchProviders.SearchProviders;
import com.project.website.canvas.shared.data.VideoData;

public class VideoToolFactory extends CanvasToolFactoryBase<VideoTool>
{
    //TODO: Set a better uniqueId.
    public static final String UNIQUE_ID = "VideoToolFactory";

    public VideoTool create()
    {
        VideoTool videoTool = new VideoTool(SearchProviders.getDefaultVideoSearchProviders());
        videoTool.setValue(new VideoData(UNIQUE_ID));
        return videoTool;
    }

    @Override
    public String getFactoryId()
    {
        return VideoToolFactory.UNIQUE_ID;
    }
}