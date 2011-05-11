package com.project.canvas.shared.data;

import javax.persistence.Id;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;

@Entity
public class Task extends ElementData {
	public @Id Long id;
	public String description;
	public Key<TaskListData> taskList;
}
