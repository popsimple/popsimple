package com.project.canvas.client.canvastools.image;

import java.util.ArrayList;

import com.project.canvas.client.canvastools.base.CanvasToolFactoryBase;
import com.project.canvas.client.shared.searchProviders.bing.BingSearchProvider;
import com.project.canvas.client.shared.searchProviders.flickr.FlickrSearchProvider;
import com.project.canvas.client.shared.searchProviders.interfaces.ImageSearchProvider;
import com.project.canvas.shared.ApiKeys;
import com.project.canvas.shared.data.MediaData;

public class ImageToolFactory extends CanvasToolFactoryBase<ImageTool> 
{
    public static final String UNIQUE_ID = "ImageToolFactory";
    
    public ImageTool create() 
    {
        ImageTool imageTool = new ImageTool(searchProvides);
        imageTool.setValue(new MediaData(UNIQUE_ID));
        return imageTool;
    }

    private static final ArrayList<ImageSearchProvider> searchProvides = createBuiltinProviders();

  //TODO: Change to ImageSearchProvider
    private static ArrayList<ImageSearchProvider> createBuiltinProviders()
    {
        //TODO: Change to ImageSearchProvider
        ArrayList<ImageSearchProvider> searchProvides = new ArrayList<ImageSearchProvider>();
        searchProvides.add(new BingSearchProvider(ApiKeys.BING_SEARCH));
        searchProvides.add(new FlickrSearchProvider(ApiKeys.FLICKR_SEARCH));
        return searchProvides;
    }
}