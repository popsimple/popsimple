package com.tutorials.hellosomeone.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class TaskListWidget extends Composite {

	private static TaskListWidgetUiBinder uiBinder = GWT
			.create(TaskListWidgetUiBinder.class);

	interface TaskListWidgetUiBinder extends UiBinder<Widget, TaskListWidget>	{
	}
	
	@UiField
	HTMLPanel panelTaskList;
	
	@UiField
	Button buttonAdd;
	
	public TaskListWidget() 
	{
		initWidget(uiBinder.createAndBindUi(this));
		
		this.AddTaskWidget();
		
		buttonAdd.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				AddTaskWidget();
			}
		});
	}
	
	private void AddTaskWidget()
	{
		TaskWidget taskWidget = new TaskWidget();
		taskWidget.AddRemoveClickHandler(new RemoveTaskClickedHandler() 
		{
			@Override
			public void onTaskRemoveClicked(TaskRemoveClickedEvent event) 
			{
				// TODO Auto-generated method stub
				RemoveTaskWidget(event.getTaskWidget());
			}
		});
		panelTaskList.add(taskWidget);
	}
	
	private void RemoveTaskWidget(TaskWidget taskWidget)
	{
		panelTaskList.remove(taskWidget);
	}
}
