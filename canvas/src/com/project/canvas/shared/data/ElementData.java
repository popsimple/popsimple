package com.project.canvas.shared.data;

import java.io.Serializable;

import javax.jdo.annotations.Embedded;

import com.google.code.twig.annotation.Id;
import com.google.gwt.user.client.rpc.IsSerializable;

public class ElementData implements Serializable, IsSerializable {
    private static final long serialVersionUID = 1L;
    @Id
    public Long id;
    @Embedded
    public Transform2D transform;
    
    public int zIndex = 0;
}
