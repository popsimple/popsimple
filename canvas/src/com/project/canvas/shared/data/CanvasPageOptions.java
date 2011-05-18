package com.project.canvas.shared.data;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class CanvasPageOptions implements Serializable, IsSerializable {
    private static final long serialVersionUID = 1L;

    public String title = "";
    public String backgroundImageURL = "";
    public String backgroundRepeat = "no-repeat";
    public String backgroundSize = "";
    public String backgroundPosition = "center center";
}
