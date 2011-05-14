package com.project.canvas.shared.data;

import java.util.ArrayList;
import java.util.List;

import com.google.code.twig.annotation.Id;

public class TaskListData extends ElementData {
	private static final long serialVersionUID = 1L;

	@Id
	public int id;
	public String title;
	public List<Task> tasks  = new ArrayList<Task>();
}
