package com.project.canvas.shared.data;

import java.io.Serializable;

import com.google.code.twig.annotation.Id;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.project.canvas.shared.contracts.ICloneable;

public class TaskData implements Serializable, IsSerializable, ICloneable {
    private static final long serialVersionUID = 1L;

    public @Id
    Long id;
    public String description;
    public boolean completed;
    public String imageUrl;
	
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
