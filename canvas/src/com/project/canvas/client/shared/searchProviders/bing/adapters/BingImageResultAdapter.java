package com.project.canvas.client.shared.searchProviders.bing.adapters;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.project.canvas.client.shared.searchProviders.ImageInfoImpl;
import com.project.canvas.client.shared.searchProviders.interfaces.ImageInfo;
import com.project.canvas.client.shared.searchProviders.interfaces.ImageResult;
import com.project.gwtbing.client.ImageSearch.Thumbnail;

public class BingImageResultAdapter implements ImageResult 
{
    com.project.gwtbing.client.ImageSearch.ImageResult _bingImageResult = null;
    
    public BingImageResultAdapter(com.project.gwtbing.client.ImageSearch.ImageResult bingImageResult)
    {
        this._bingImageResult = bingImageResult;
    }
    
    @Override
    public String getUrl() {
        return this._bingImageResult.getUrl();
    }

    @Override
    public String getTitle() {
        return this._bingImageResult.getTitle();
    }

    @Override
    public void getImageSizes(AsyncCallback<ArrayList<ImageInfo>> callback) {
        ArrayList<ImageInfo> imageInfoList = new ArrayList<ImageInfo>();
        
        Thumbnail thumbnail = this._bingImageResult.getThumbnail();
        imageInfoList.add(new ImageInfoImpl(thumbnail.getUrl(),
                thumbnail.getWidth(), thumbnail.getHeight()));
        imageInfoList.add(new ImageInfoImpl(this._bingImageResult.getMediaUrl(),
                this._bingImageResult.getWidth(), this._bingImageResult.getHeight()));
        
        callback.onSuccess(imageInfoList);
    }

    @Override
    public String getThumbnailUrl() {
        
        return this._bingImageResult.getThumbnail().getUrl();
    }
}
