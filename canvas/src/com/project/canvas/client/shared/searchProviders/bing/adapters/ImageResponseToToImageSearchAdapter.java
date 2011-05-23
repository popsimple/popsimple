package com.project.canvas.client.shared.searchProviders.bing.adapters;

import java.util.ArrayList;

import com.google.gwt.core.client.JsArray;
import com.project.canvas.client.shared.searchProviders.interfaces.ImageResult;
import com.project.canvas.client.shared.searchProviders.interfaces.ImageSearchResult;
import com.project.gwtbing.client.ImageSearch.ImageResponse;

public class ImageResponseToToImageSearchAdapter implements ImageSearchResult 
{
    private ImageResponse _imageResponse = null;
    private int _pageSize = 0;
    
    public ImageResponseToToImageSearchAdapter(int pageSize, ImageResponse imageResponse)
    {
        this._pageSize = pageSize;
        this._imageResponse = imageResponse;
    }
    
    @Override
    public int getTotalPages() {
        if (0 == this._pageSize)
        {
            return 0;
        }
        //TODO: Consider a better calculation.
        return (int)Math.ceil((double)this._imageResponse.getTotal() / this._pageSize);
    }

    @Override
    public int getCurrentPage() {
        int resultCount = this._imageResponse.getOffset() + 
        this._imageResponse.getResults().length();
      //TODO: Consider a better calculation.
        return (int)(Math.ceil((double)this._pageSize / (double)resultCount));
    }

    @Override
    public ArrayList<ImageResult> getImageResults() {
        ArrayList<ImageResult> resultList = new ArrayList<ImageResult>();
        JsArray<com.project.gwtbing.client.ImageSearch.ImageResult> bingImageResults = this._imageResponse.getResults();
        for (int index = 0; index < bingImageResults.length(); index++)
        {
            resultList.add(new BingImageResultAdapter(bingImageResults.get(index)));
        }
        return resultList;
    }

}
