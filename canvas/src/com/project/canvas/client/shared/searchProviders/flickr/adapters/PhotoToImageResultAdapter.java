package com.project.canvas.client.shared.searchProviders.flickr.adapters;

import java.util.ArrayList;

import com.ghusse.dolomite.flickr.Credentials;
import com.ghusse.dolomite.flickr.Photo;
import com.ghusse.dolomite.flickr.PhotoSize;
import com.ghusse.dolomite.flickr.PhotoSizesResponse;
import com.ghusse.dolomite.flickr.PhotoSizesResponse.PhotoSizeResponse;
import com.ghusse.dolomite.flickr.photos.GetSizes;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.project.canvas.client.shared.searchProviders.ImageInfoImpl;
import com.project.canvas.client.shared.searchProviders.flickr.FlickrSearchErrorException;
import com.project.canvas.client.shared.searchProviders.interfaces.MediaInfo;
import com.project.canvas.client.shared.searchProviders.interfaces.MediaResult;

public class PhotoToImageResultAdapter implements MediaResult 
{
    private GetSizes photoSizesGetter = null;
    private Photo photo = null;
    
    public PhotoToImageResultAdapter(Credentials credentials, Photo photo)
    {
        this.photoSizesGetter = new GetSizes(credentials);
        this.photo = photo;
    }

    @Override
    public String getUrl() {
        return photo.getPageUrl();
    }

    @Override
    public String getTitle() {
        return photo.getTitle();
    }

    @Override
    public String getThumbnailUrl() {
        return this.photo.getSourceUrl(PhotoSize.THUMBNAIL);
    }

    @Override
    public void getMediaSizes(final AsyncCallback<ArrayList<MediaInfo>> callback) 
    {
        this.photoSizesGetter.setPhoto(this.photo);
        this.photoSizesGetter.send(new AsyncCallback<PhotoSizesResponse>() {
            
            @Override
            public void onSuccess(PhotoSizesResponse result) {
                if (false == result.getStatus())
                {
                    callback.onFailure(new FlickrSearchErrorException(result.getMessage()));
                    return;
                }
                callback.onSuccess(createImageInfoArray(result));
            }
            
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
        });
    }
    
    private ArrayList<MediaInfo> createImageInfoArray(PhotoSizesResponse sizeResult)
    {
        ArrayList<MediaInfo> imageList = new ArrayList<MediaInfo>();
        for (PhotoSizeResponse sizeResponse : sizeResult.getSizes())
        {
            imageList.add(new ImageInfoImpl(sizeResponse.getSource(), 
                    sizeResponse.getWidth(), sizeResponse.getHeight()));
        }
        return imageList;
    }
}
