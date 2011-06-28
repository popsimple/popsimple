package com.project.website.canvas.client.shared.searchProviders.youtube.adapters;

import java.util.ArrayList;

import com.google.gwt.core.client.JsArray;
import com.project.gwtyoutube.client.YouTubeData;
import com.project.gwtyoutube.client.YouTubeItem;
import com.project.gwtyoutube.client.YouTubeResult;
import com.project.website.canvas.client.shared.searchProviders.interfaces.MediaResult;
import com.project.website.canvas.client.shared.searchProviders.interfaces.MediaSearchResult;

public class YouTubeResultAdapter implements MediaSearchResult 
{
    private YouTubeResult _youTubeResult;

    public YouTubeResultAdapter(YouTubeResult youTubeResult)
    {
        this._youTubeResult = youTubeResult;
    }
    

    @Override
    public int getTotalPages() {
        YouTubeData youTubeData = this._youTubeResult.getData();
        if (0 == youTubeData.getItemsPerPage())
        {
            return 0;
        }
        //TODO: Consider a better calculation.
        return (int)Math.ceil((double)youTubeData.getTotalItems() / (double)youTubeData.getItemsPerPage());
    }

    @Override
    public int getCurrentPage() 
    {
        YouTubeData youTubeData = this._youTubeResult.getData();
        int resultCount = youTubeData.getStartIndex() + 
            youTubeData.getItems().length();
        //TODO: Consider a better calculation.
        return (int)(Math.ceil((double)youTubeData.getItemsPerPage() / (double)resultCount));
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
