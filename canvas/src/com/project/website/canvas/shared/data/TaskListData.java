package com.project.website.canvas.shared.data;

import java.util.ArrayList;
import java.util.List;

import com.project.shared.utils.CloneableUtils;

public class TaskListData extends ElementData {

    protected TaskListData()
    {
    }

    public TaskListData(String factoryUniqueId) {
        super(factoryUniqueId);
    }
    private static final long serialVersionUID = 1L;

    public String title;
    public List<TaskData> tasks = new ArrayList<TaskData>();

    @Override
    public Object createInstance() {
    	return new TaskListData();
    }

    @Override
    public void copyTo(Object object) {
    	super.copyTo(object);
    	TaskListData copy = (TaskListData)object;
    	copy.title = this.title;
    	for (TaskData taskData : tasks)
    	{
    		copy.tasks.add((TaskData)CloneableUtils.clone(taskData));
    	}
    }
}
