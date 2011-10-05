package com.project.website.canvas.shared.data;

import java.io.Serializable;

import com.google.appengine.api.datastore.Text;
import com.google.code.twig.annotation.Id;
import com.google.code.twig.annotation.Type;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.project.shared.interfaces.ICloneable;

public class TaskData implements Serializable, IsSerializable, ICloneable<TaskData>
{
    private static final long serialVersionUID = 1L;

    @Id
    Long id;

    public String description = "";
    public boolean completed = false;

    //GWT datastore doesn't support String members that are longer than 500 chars.
    //and TaskData.ImageUrl can be embedded url.
    @Type(Text.class)
    public String imageUrl = "";

    public TaskData(TaskData other)
    {
        this();
        this.description = other.description;
        this.completed = other.completed;
        this.imageUrl = other.imageUrl;
    }

    public TaskData() { }

    @Override
    public TaskData getClone()
    {
        return new TaskData(this);
    }

}
