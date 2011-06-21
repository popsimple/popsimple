package com.project.canvas.shared.data;

import java.io.Serializable;
import com.google.code.twig.annotation.Embedded;
import com.google.code.twig.annotation.Id;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.project.canvas.shared.CloneableUtils;
import com.project.canvas.shared.contracts.ICloneable;

public class ElementData implements Serializable, IsSerializable, ICloneable {
    private static final long serialVersionUID = 1L;

    @Id
    public Long id;

    public String factoryUniqueId;

    @Embedded
    public Transform2D transform = new Transform2D();

    public int zIndex = 0;

    protected ElementData(){
    }

    public ElementData(String factoryUniqueId)
    {
        this.factoryUniqueId = factoryUniqueId;
    }

    @Override
    public Object createInstance()
    {
    	return new ElementData();
    }

    @Override
    public void copyTo(Object object)
    {
    	ElementData copy = (ElementData)object;
    	copy.factoryUniqueId = this.factoryUniqueId;
    	copy.transform = (Transform2D)CloneableUtils.clone(this.transform);
    }
}
