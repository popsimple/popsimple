package com.project.canvas.client.shared.searchProviders.bing.adapters;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.project.canvas.client.shared.searchProviders.ImageInfoImpl;
import com.project.canvas.client.shared.searchProviders.interfaces.MediaInfo;
import com.project.canvas.client.shared.searchProviders.interfaces.MediaResult;
import com.project.gwtbing.client.imagesearch.Thumbnail;

public class BingImageResultAdapter implements MediaResult 
{
    com.project.gwtbing.client.imagesearch.ImageResult _bingImageResult = null;
    
    public BingImageResultAdapter(com.project.gwtbing.client.imagesearch.ImageResult bingImageResult)
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
    public void getMediaSizes(AsyncCallback<ArrayList<MediaInfo>> callback) {
        ArrayList<MediaInfo> imageInfoList = new ArrayList<MediaInfo>();
        
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
