package com.project.website.canvas.client.shared.searchProviders.youtube.adapters;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.project.gwtyoutube.client.YouTubeItem;
import com.project.gwtyoutube.client.YouTubeThumbnail;
import com.project.shared.utils.StringUtils;
import com.project.website.canvas.client.shared.searchProviders.MediaInfoImpl;
import com.project.website.canvas.client.shared.searchProviders.interfaces.MediaInfo;
import com.project.website.canvas.client.shared.searchProviders.interfaces.MediaResult;

public class YouTubeItemToMediaResultAdapter implements MediaResult  
{
    private final static int DEFAULT_VIDEO_WIDTH = 425;
    private final static int DEFAULT_VIDEO_HEIGHT = 349;
    private YouTubeItem _youTubeItem;

    public YouTubeItemToMediaResultAdapter(YouTubeItem youTubeItem)
    {
        this._youTubeItem = youTubeItem;
    }
    
    
    @Override
    public String getUrl() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getTitle() {
        return this._youTubeItem.getTitle();
    }

    @Override
    public void getMediaSizes(AsyncCallback<ArrayList<MediaInfo>> callback) {
        ArrayList<MediaInfo> mediaSizes = new ArrayList<MediaInfo>();
        mediaSizes.add(new MediaInfoImpl(this._youTubeItem.getEmbeddedUrl(),
                "Standard", DEFAULT_VIDEO_WIDTH, DEFAULT_VIDEO_HEIGHT));
        callback.onSuccess(mediaSizes);
    }

    @Override
    public String getThumbnailUrl() {
        YouTubeThumbnail thumbnail = this._youTubeItem.getThumbnail();
        
        return StringUtils.defaultIfNullOrEmpty(thumbnail.getDefaultUrl(),
                StringUtils.defaultIfNullOrEmpty(thumbnail.getSQDefaultUrl(),
                        thumbnail.getHQDefaultUrl()));
    }
}
