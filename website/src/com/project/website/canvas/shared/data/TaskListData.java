package com.project.website.canvas.shared.data;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.project.shared.interfaces.ICloneable;

public class TaskListData extends ElementData implements ICloneable<TaskListData>
{

    private static final long serialVersionUID = 1L;

    public String title;
    public List<TaskData> tasks = new ArrayList<TaskData>();

    protected TaskListData()
    {
        super();
    }

    public TaskListData(String factoryUniqueId) {
        super(factoryUniqueId);
    }
    public TaskListData(TaskListData taskListData)
    {
        super(taskListData);
        this.title = taskListData.title;
        this.tasks = Lists.transform(taskListData.tasks, new Function<TaskData,TaskData>(){
            @Override public TaskData apply(TaskData input) {
                return input.getClone();
            }});
    }


    @Override
    public ICloneable<? extends ElementData> getCloneable()
    {
        return this;
    }

    @Override
    public TaskListData getClone()
    {
        return new TaskListData(this);
    }
}
