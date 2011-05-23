package com.project.canvas.client.canvastools.Image;

import com.project.canvas.client.canvastools.base.CanvasToolFactoryBase;
import com.project.canvas.client.shared.searchProviders.bing.BingSearchProvider;
import com.project.canvas.client.shared.searchProviders.flickr.FlickrSearchProvider;

public class ImageToolFactory extends CanvasToolFactoryBase<ImageTool> {

    public ImageTool create() 
    {
        //TODO: Extract.
//        return new ImageTool(new FlickrSearchProvider("023322961d08d84124ba870f1adce55b"));
        return new ImageTool(new BingSearchProvider("68910216D550D46A50E65B86A92F0FC245EFE6B7"));

    }
}