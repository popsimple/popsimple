package com.project.canvas.client.canvastools.image;

import java.util.ArrayList;

import com.project.canvas.client.canvastools.base.CanvasToolFactoryBase;
import com.project.canvas.client.shared.searchProviders.bing.BingSearchProvider;
import com.project.canvas.client.shared.searchProviders.flickr.FlickrSearchProvider;
import com.project.canvas.client.shared.searchProviders.interfaces.ImageSearchProvider;
import com.project.canvas.shared.ApiKeys;

public class ImageToolFactory extends CanvasToolFactoryBase<ImageTool> 
{
    public ImageTool create() 
    {
        return new ImageTool(searchProvides);
    }

    private static final ArrayList<ImageSearchProvider> searchProvides = createBuiltinProviders();

    private static ArrayList<ImageSearchProvider> createBuiltinProviders()
    {
        ArrayList<ImageSearchProvider> searchProvides = new ArrayList<ImageSearchProvider>();
        searchProvides.add(new BingSearchProvider(ApiKeys.BING_SEARCH));
        searchProvides.add(new FlickrSearchProvider(ApiKeys.FLICKR_SEARCH));
        return searchProvides;
    }
}