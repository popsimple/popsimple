package com.project.canvas.shared.data;

public class TextData extends ElementData {
    protected TextData()
    {
    }
    
    public TextData(String factoryUniqueId) {
        super(factoryUniqueId);
    }
    private static final long serialVersionUID = 1L;
    public String text;
    public int width; // height is determined by text contents.
}
