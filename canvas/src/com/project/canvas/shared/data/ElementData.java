package com.project.canvas.shared.data;

import java.io.Serializable;

import javax.jdo.annotations.Embedded;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.vercer.engine.persist.annotation.Embed;
import com.vercer.engine.persist.annotation.Id;

public class ElementData implements Serializable, IsSerializable {
    private static final long serialVersionUID = 1L;
    @Id
    public Long id;
    @Embedded
    @Embed
    public Transform2D transform;
    
    public int zIndex = 0;
}
