package com.project.canvas.shared.data;

import java.util.ArrayList;
import java.util.List;

public class TaskListData extends ElementData {
    private static final long serialVersionUID = 1L;

    public String title;
    public List<TaskData> tasks = new ArrayList<TaskData>();
}
