package com.project.canvas.shared.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.vercer.engine.persist.annotation.Embed;
import com.vercer.engine.persist.annotation.Id;

public class CanvasPage implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    public Long id;
    @Embed
    public CanvasPageOptions options = new CanvasPageOptions();

    public List<ElementData> elements = new ArrayList<ElementData>();
}
