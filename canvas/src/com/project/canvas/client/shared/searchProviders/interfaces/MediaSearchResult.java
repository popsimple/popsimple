package com.project.canvas.client.shared.searchProviders.interfaces;

import java.util.ArrayList;

public interface MediaSearchResult 
{
    int getTotalPages();
    int getCurrentPage();
    ArrayList<MediaResult> getMediaResults();
}
