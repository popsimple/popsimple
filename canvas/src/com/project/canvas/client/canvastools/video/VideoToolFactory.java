package com.project.canvas.client.canvastools.video;

import java.util.ArrayList;

import com.project.canvas.client.canvastools.base.CanvasToolFactoryBase;
import com.project.canvas.client.shared.searchProviders.interfaces.VideoSearchProvider;
import com.project.canvas.client.shared.searchProviders.youtube.YouTubeSearchProvider;
import com.project.canvas.shared.data.MediaData;

public class VideoToolFactory extends CanvasToolFactoryBase<VideoTool> 
{
    //TODO: Set a better uniqueId.
    public static final String UNIQUE_ID = "VideoToolFactory";
    
    public VideoTool create() 
    {
        VideoTool videoTool = new VideoTool(searchProvides);
        videoTool.setValue(new MediaData(UNIQUE_ID));
        return videoTool;
    }

    private static final ArrayList<VideoSearchProvider> searchProvides = createBuiltinProviders();

  //TODO: Change to ImageSearchProvider
    private static ArrayList<VideoSearchProvider> createBuiltinProviders()
    {
        //TODO: Change to ImageSearchProvider
        ArrayList<VideoSearchProvider> searchProvides = new ArrayList<VideoSearchProvider>();
        searchProvides.add(new YouTubeSearchProvider());
        return searchProvides;
    }
}