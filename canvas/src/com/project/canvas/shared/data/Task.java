package com.project.canvas.shared.data;

import java.io.Serializable;
import java.util.List;

import com.google.code.twig.annotation.Id;
import com.google.gwt.user.client.rpc.IsSerializable;

public class Task implements Serializable , IsSerializable {
	private static final long serialVersionUID = 1L;

	public @Id Long id;
	public String description;
	public boolean completed;
	public List<TaskListData> tasks;
}
