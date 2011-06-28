package com.project.website.canvas.shared.data;

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
    
    @Override
    public Object createInstance() {
    	return new TextData();
    }
    
    @Override
    public void copyTo(Object object) {
    	super.copyTo(object);
    	TextData copy = (TextData)object;
    	copy.text = this.text;
    	copy.width = this.width;
    }
}
