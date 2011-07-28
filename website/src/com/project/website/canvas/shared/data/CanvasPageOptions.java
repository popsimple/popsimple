package com.project.website.canvas.shared.data;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class CanvasPageOptions implements Serializable, IsSerializable {
    private static final long serialVersionUID = 1L;

    public String title = "";
    public ImageInformation backgroundImage = new ImageInformation();

    public CanvasPageOptions()
    {
    }
}