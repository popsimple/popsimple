package com.project.canvas.client.shared.searchProviders.youtube.adapters;

import java.util.ArrayList;

import com.google.gwt.core.client.JsArray;
import com.project.canvas.client.shared.searchProviders.interfaces.ImageResult;
import com.project.canvas.client.shared.searchProviders.interfaces.ImageSearchResult;
import com.project.canvas.client.shared.searchProviders.youtube.YouTubeItem;
import com.project.canvas.client.shared.searchProviders.youtube.YouTubeResult;

public class YouTubeResultToImageSearchAdapter implements ImageSearchResult 
{
    private YouTubeResult _youTubeResult;

    public YouTubeResultToImageSearchAdapter(YouTubeResult youTubeResult)
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
    public ArrayList<ImageResult> getImageResults() {
        ArrayList<ImageResult> resultList = new ArrayList<ImageResult>();
        JsArray<YouTubeItem> youTubeResults = this._youTubeResult.getData().getItems();
        for (int index = 0; index < youTubeResults.length(); index++)
        {
            resultList.add(new YouTubeItemToImageResultAdapter(youTubeResults.get(index)));
        }
        return resultList;
    }

}
