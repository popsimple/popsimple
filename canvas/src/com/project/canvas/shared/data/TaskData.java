package com.project.canvas.shared.data;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.vercer.engine.persist.annotation.Id;

public class TaskData implements Serializable, IsSerializable {
    private static final long serialVersionUID = 1L;

    public @Id
    Long id;
    public String _description;
    public boolean _completed;
    public String _imageUrl;
}
