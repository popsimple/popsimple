package com.project.canvas.shared.data;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ImageInformation implements Serializable, IsSerializable {
    private static final long serialVersionUID = 1L;

    public String url;
    public boolean repeat = false;
    public boolean center = false;
    public boolean stretchWidth = false;
    public boolean stretchHeight = false;
    public Point2D size = new Point2D();
}
