package com.project.canvas.client.shared.searchProviders;

import java.util.ArrayList;

public interface ImageSearchResult 
{
    int getTotalPages();
    int getCurrentPage();
    ArrayList<ImageResult> getImageResults();
}
