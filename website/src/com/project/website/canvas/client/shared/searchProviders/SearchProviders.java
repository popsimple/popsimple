package com.project.website.canvas.client.shared.searchProviders;

import java.util.ArrayList;
import java.util.List;

import com.project.website.canvas.client.shared.searchProviders.flickr.FlickrSearchProvider;
import com.project.website.canvas.client.shared.searchProviders.interfaces.ImageSearchProvider;
import com.project.website.canvas.client.shared.searchProviders.interfaces.VideoSearchProvider;
import com.project.website.canvas.client.shared.searchProviders.youtube.YouTubeSearchProvider;
import com.project.website.canvas.shared.ApiKeys;

//TODO: Replace this class with appropriate configuration.
public class SearchProviders
{
    private static final List<ImageSearchProvider> _defaultImageSearchProviders =
        createDefaultImageSearchProviderList();
    private static final List<VideoSearchProvider> _defaultVideoSearchProviders =
        createDefaultVideoSearchProviderList();

    private static List<ImageSearchProvider> createDefaultImageSearchProviderList()
    {
        ArrayList<ImageSearchProvider> searchProvides = new ArrayList<ImageSearchProvider>();
        // TODO: Fix the bing search API client, it is broken since Microsfot moved it
        // to Azure Marketplace and changed the API (now it requires OAuth or HTTP authentication)
        // searchProvides.add(new BingSearchProvider(ApiKeys.BING_SEARCH));
        searchProvides.add(new FlickrSearchProvider(ApiKeys.FLICKR_SEARCH));
        return searchProvides;
    }

    private static List<VideoSearchProvider> createDefaultVideoSearchProviderList()
    {
        ArrayList<VideoSearchProvider> searchProvides = new ArrayList<VideoSearchProvider>();
        searchProvides.add(new YouTubeSearchProvider());
        return searchProvides;
    }

    public static List<ImageSearchProvider> getDefaultImageSearchProviders()
    {
        return SearchProviders._defaultImageSearchProviders;
    }

    public static List<VideoSearchProvider> getDefaultVideoSearchProviders()
    {
        return SearchProviders._defaultVideoSearchProviders;
    }
}
