package com.project.canvas.shared.data;

public class MediaData extends ElementData {
    
    protected MediaData()
    {
    }
    
    public MediaData(String factoryUniqueId) {
        super(factoryUniqueId);
    }

    private static final long serialVersionUID = 1L;

    public String url;
}
