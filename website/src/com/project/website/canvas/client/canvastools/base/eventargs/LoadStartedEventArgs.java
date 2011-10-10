package com.project.website.canvas.client.canvastools.base.eventargs;

public class LoadStartedEventArgs
{
    public static final boolean DIM_BACKGROUND_DEFAULT = true;

    public boolean dimBackground = DIM_BACKGROUND_DEFAULT;

    public LoadStartedEventArgs()
    {
        this(DIM_BACKGROUND_DEFAULT);
    }

    public LoadStartedEventArgs(boolean dimBackground)
    {
        this.dimBackground = dimBackground;
    }
}
