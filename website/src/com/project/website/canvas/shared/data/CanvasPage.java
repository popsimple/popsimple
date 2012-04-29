package com.project.website.canvas.shared.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.code.twig.annotation.Embedded;
import com.google.code.twig.annotation.Id;

public class CanvasPage implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    public Long id;
    @Embedded
    public CanvasPageOptions options = new CanvasPageOptions();

    public List<ElementData> elements = new ArrayList<ElementData>();
    
    public String key = null; 
}
