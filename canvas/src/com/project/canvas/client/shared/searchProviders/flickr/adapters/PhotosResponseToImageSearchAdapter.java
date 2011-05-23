package com.project.canvas.client.shared.searchProviders.flickr.adapters;

import java.util.ArrayList;

import com.ghusse.dolomite.flickr.Credentials;
import com.ghusse.dolomite.flickr.Photo;
import com.ghusse.dolomite.flickr.PhotosResponse;
import com.google.gwt.core.client.JsArray;
import com.project.canvas.client.shared.searchProviders.interfaces.ImageResult;
import com.project.canvas.client.shared.searchProviders.interfaces.ImageSearchResult;

public class PhotosResponseToImageSearchAdapter implements ImageSearchResult 
{
    private PhotosResponse photosResponse = null;
    private Credentials credentials = null;
    
    public PhotosResponseToImageSearchAdapter(Credentials credentials, PhotosResponse photosResponse)
    {
        this.credentials = credentials;
        this.photosResponse = photosResponse;
    }

    @Override
    public int getTotalPages() 
    {
        return this.photosResponse.getPhotosPage().getPagesCount();
    }

    @Override
    public int getCurrentPage() 
    {
        return this.photosResponse.getPhotosPage().getPage();
    }

    @Override
    public ArrayList<ImageResult> getImageResults() 
    {
        ArrayList<ImageResult> imageResultList = new ArrayList<ImageResult>();
        JsArray<Photo> photoArray = this.photosResponse.getPhotosPage().getPhotos();
        for (int index = 0; index < photoArray.length(); index++)
        {
            imageResultList.add(new PhotoToImageResultAdapter(this.credentials, 
                    photoArray.get(index)));
        }
        return imageResultList;
    }
    
}
