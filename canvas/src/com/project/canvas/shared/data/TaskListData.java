package com.project.canvas.shared.data;

import java.util.ArrayList;
import java.util.List;

public class TaskListData extends ElementData {
	private static final long serialVersionUID = 1L;

	public String _title;
	public List<TaskData> _tasks  = new ArrayList<TaskData>();
}
