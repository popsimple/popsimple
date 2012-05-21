package com.project.website.canvas.shared.data;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.project.shared.data.Point2D;

public class CanvasPageOptions implements Serializable, IsSerializable {
    private static final long serialVersionUID = 1L;

    public String title = "";
    public ImageInformation backgroundImage = new ImageInformation();
    public Point2D size = new Point2D();

    public CanvasPageOptions()
    {
    }
}
