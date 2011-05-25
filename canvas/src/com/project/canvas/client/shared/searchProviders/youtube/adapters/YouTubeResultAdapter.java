package com.project.canvas.client.shared.searchProviders.youtube.adapters;

import java.util.ArrayList;

import com.google.gwt.core.client.JsArray;
import com.project.canvas.client.shared.searchProviders.interfaces.MediaResult;
import com.project.canvas.client.shared.searchProviders.interfaces.MediaSearchResult;
import com.project.gwtyoutube.client.YouTubeItem;
import com.project.gwtyoutube.client.YouTubeResult;

public class YouTubeResultAdapter implements MediaSearchResult 
{
    private YouTubeResult _youTubeResult;

    public YouTubeResultAdapter(YouTubeResult youTubeResult)
    {
        this._youTubeResult = youTubeResult;
    }
    

    @Override
    public int getTotalPages() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getCurrentPage() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ArrayList<MediaResult> getMediaResults() {
        ArrayList<MediaResult> resultList = new ArrayList<MediaResult>();
        JsArray<YouTubeItem> youTubeResults = this._youTubeResult.getData().getItems();
        for (int index = 0; index < youTubeResults.length(); index++)
        {
            resultList.add(new YouTubeItemToMediaResultAdapter(youTubeResults.get(index)));
        }
        return resultList;
    }

}
