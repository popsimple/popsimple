package com.project.website.canvas.shared.data;

import com.google.appengine.api.datastore.Text;
import com.google.code.twig.annotation.Type;
import com.project.shared.interfaces.ICloneable;



public class TextData extends ElementData implements ICloneable<TextData>
{
    private static final long serialVersionUID = 1L;

    @Type(Text.class)
    public String innerHtml;

    public String cssText;

    protected TextData() {
        super();
    }

    public TextData(String factoryUniqueId) {
        super(factoryUniqueId);
    }

    public TextData(TextData other) {
        super(other);
        this.innerHtml = other.innerHtml;
        this.cssText = other.cssText;
    }

    @Override
    public ICloneable<? extends ElementData> getCloneable()
    {
        return this;
    }

    @Override
    public TextData getClone()
    {
        return new TextData(this);
    }
}
