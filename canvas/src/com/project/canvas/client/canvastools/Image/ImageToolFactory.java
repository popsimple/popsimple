package com.project.canvas.client.canvastools.Image;

import java.util.ArrayList;

import com.project.canvas.client.canvastools.base.CanvasToolFactoryBase;
import com.project.canvas.client.shared.searchProviders.bing.BingSearchProvider;
import com.project.canvas.client.shared.searchProviders.flickr.FlickrSearchProvider;
import com.project.canvas.client.shared.searchProviders.interfaces.ImageSearchProvider;

public class ImageToolFactory extends CanvasToolFactoryBase<ImageTool> {

    public ImageTool create() 
    {
        ArrayList<ImageSearchProvider> searchProvides = new ArrayList<ImageSearchProvider>();
        searchProvides.add(new BingSearchProvider("68910216D550D46A50E65B86A92F0FC245EFE6B7"));
        searchProvides.add(new FlickrSearchProvider("023322961d08d84124ba870f1adce55b"));
        return new ImageTool(searchProvides);
    }
}