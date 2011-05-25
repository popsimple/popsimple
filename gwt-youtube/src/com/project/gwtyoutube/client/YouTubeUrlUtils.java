package com.project.gwtyoutube.client;

public class YouTubeUrlUtils 
{
    private static final String EMBEDDED_URL = "http://www.youtube.com/embed/";
    //TODO: Shouldn't be here, currently supports transparent window.
    //NOTE: according to: http://www.electrictoolbox.com/float-div-youtube-iframe/
    private static final String WMODE_TRANSPARENT = "&wmode=transparent";
    
    public static String buildEmbeddedUrl(String videoId)
    {
        return EMBEDDED_URL + videoId + WMODE_TRANSPARENT;
    }
}
