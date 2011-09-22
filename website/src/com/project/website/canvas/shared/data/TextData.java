package com.project.website.canvas.shared.data;

import com.google.appengine.api.datastore.Text;
import com.google.code.twig.annotation.Type;



public class TextData extends ElementData
{
    private static final long serialVersionUID = 1L;

    protected TextData() {}

    public TextData(String factoryUniqueId) {
        super(factoryUniqueId);
    }

    @Type(Text.class)
    public String innerHtml;
    
    public String cssText;

    @Override
    public Object createInstance() {
    	return new TextData();
    }

    @Override
    public void copyTo(Object object) {
    	super.copyTo(object);
    	TextData copy = (TextData)object;
    	copy.innerHtml = this.innerHtml;
    	copy.cssText = this.cssText;
    }
}
