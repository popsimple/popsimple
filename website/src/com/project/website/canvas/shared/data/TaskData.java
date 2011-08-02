package com.project.website.canvas.shared.data;

import java.io.Serializable;

import com.google.appengine.api.datastore.Text;
import com.google.code.twig.annotation.Id;
import com.google.code.twig.annotation.Type;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.project.shared.interfaces.ICloneable;

public class TaskData implements Serializable, IsSerializable, ICloneable {
    private static final long serialVersionUID = 1L;

    public @Id
    Long id;
    public String description = "";
    public boolean completed = false;

    //GWT datastore doesn't support String members that are longer than 500 chars.
    //and TaskData.ImageUrl can be embedded url.
    @Type(Text.class)
    public String imageUrl = "";

    @Override
	public Object createInstance() {
		return new TaskData();
	}
	@Override
	public void copyTo(Object object) {
		TaskData copy = (TaskData)this.createInstance();
		copy.description = this.description;
		copy.completed = this.completed;
		copy.imageUrl = this.imageUrl;
	}
}
