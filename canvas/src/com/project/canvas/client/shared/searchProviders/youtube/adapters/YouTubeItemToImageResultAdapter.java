package com.project.canvas.client.shared.searchProviders.youtube.adapters;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.project.canvas.client.shared.searchProviders.interfaces.ImageInfo;
import com.project.canvas.client.shared.searchProviders.interfaces.ImageResult;
import com.project.canvas.client.shared.searchProviders.youtube.YouTubeItem;
import com.project.canvas.client.shared.searchProviders.youtube.YouTubeThumbnail;

public class YouTubeItemToImageResultAdapter implements ImageResult  
{
    private YouTubeItem _youTubeItem;

    public YouTubeItemToImageResultAdapter(YouTubeItem youTubeItem)
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
    public void getImageSizes(AsyncCallback<ArrayList<ImageInfo>> callback) {
        // TODO Auto-generated method stub
        
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
