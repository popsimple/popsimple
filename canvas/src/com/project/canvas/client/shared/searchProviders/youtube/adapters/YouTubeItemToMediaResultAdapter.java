package com.project.canvas.client.shared.searchProviders.youtube.adapters;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.project.canvas.client.shared.searchProviders.MediaInfoImpl;
import com.project.canvas.client.shared.searchProviders.interfaces.MediaInfo;
import com.project.canvas.client.shared.searchProviders.interfaces.MediaResult;
import com.project.gwtyoutube.client.YouTubeItem;
import com.project.gwtyoutube.client.YouTubeThumbnail;

public class YouTubeItemToMediaResultAdapter implements MediaResult  
{
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
                "Standard", 400, 400));
        callback.onSuccess(mediaSizes);
    }

    @Override
    public String getThumbnailUrl() {
        YouTubeThumbnail thumbnail = this._youTubeItem.getThumbnail();
        
        return DefaultIfEmptyOrNull(thumbnail.getDefaultUrl(),
                DefaultIfEmptyOrNull(thumbnail.getSQDefaultUrl(),
                        thumbnail.getHQDefaultUrl()));
    }
    
    
    //TODO: Move
    public String DefaultIfEmptyOrNull(String str, String defaultStr)
    {
        return IsEmptyOrNull(str) ? defaultStr : str;
    }
    
    public boolean IsEmptyOrNull(String str)
    {
        if ((null == str) || (str.isEmpty()))
        {
            return true;
        }
        return false;
    }

}
