package com.project.canvas.shared.data;

import java.io.Serializable;

import com.google.code.twig.annotation.Embedded;
import com.google.code.twig.annotation.Id;
import com.google.gwt.user.client.rpc.IsSerializable;

public class ElementData implements Serializable, IsSerializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    public Long id;
    
    public String factoryUniqueId;
    
    @Embedded
    public Transform2D transform;
    
    public int zIndex = 0;
    
    protected ElementData()
    {
    }
    
    public ElementData(String factoryUniqueId)
    {
        this.factoryUniqueId = factoryUniqueId;
    }
}
