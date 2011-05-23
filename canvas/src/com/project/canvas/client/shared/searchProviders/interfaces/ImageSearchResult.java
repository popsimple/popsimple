package com.project.canvas.client.shared.searchProviders.interfaces;

import java.util.ArrayList;

public interface ImageSearchResult 
{
    int getTotalPages();
    int getCurrentPage();
    ArrayList<ImageResult> getImageResults();
}
