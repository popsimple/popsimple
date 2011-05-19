package com.project.canvas.shared.data;

import java.io.Serializable;

import com.vercer.engine.persist.annotation.Embed;
import com.vercer.engine.persist.annotation.Id;

public class ElementData implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    public Long id;

    @Embed
    public Transform2D transform = new Transform2D();
    
    public int zIndex = 0;
}
