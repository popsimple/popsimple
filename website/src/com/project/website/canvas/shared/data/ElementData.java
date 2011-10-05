package com.project.website.canvas.shared.data;

import java.io.Serializable;

import com.google.code.twig.annotation.Embedded;
import com.google.code.twig.annotation.Id;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.project.shared.interfaces.IsCloneable;
import com.project.shared.utils.RandomUtils;

public abstract class ElementData implements Serializable, IsSerializable, IsCloneable<ElementData> {
    private static final long serialVersionUID = 1L;

    @Id
    public Long id;

    public String uniqueId;

    public String factoryUniqueId;

    @Embedded
    public Transform2D transform = new Transform2D();

    public int zIndex = 0;

    protected ElementData(){
        this.uniqueId = RandomUtils.randomString(16);
    }

    public ElementData(String factoryUniqueId)
    {
        this();
        this.factoryUniqueId = factoryUniqueId;
    }

    public ElementData(ElementData other)
    {
        this();
        this.factoryUniqueId = other.factoryUniqueId;
    }
}
