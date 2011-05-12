package com.project.canvas.client.canvastools.TaskList;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.project.canvas.client.canvastools.base.CanvasTool;
import com.project.canvas.client.canvastools.base.CanvasToolCommon;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.client.shared.events.SimpleEvent.Handler;
import com.project.canvas.shared.data.ElementData;
import com.project.canvas.shared.data.TaskListData;

public class TaskListWidget extends Composite implements CanvasTool<TaskListData> {

	private static TaskListWidgetUiBinder uiBinder = GWT
			.create(TaskListWidgetUiBinder.class);

	interface TaskListWidgetUiBinder extends UiBinder<Widget, TaskListWidget>	{
	}
	
	@UiField
	HTMLPanel panelTaskList;
	
	@UiField
	HoverTextBox title;
	
	@UiField
	Button buttonAdd;
	
	private SimpleEvent<String> killRequestedEvent = new SimpleEvent<String>();
	private ArrayList<TaskWidget> taskWidgets = new ArrayList<TaskWidget>();

	private TaskListData data;
	
	public TaskListWidget() 
	{
		initWidget(uiBinder.createAndBindUi(this));
		CanvasToolCommon.initCanvasToolWidget(this);
		
		this.AddTaskWidget();
		
		buttonAdd.addClickHandler(new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				AddTaskWidget();
			}
		});
	}


	private void AddTaskWidget()
	{
		TaskWidget taskWidget = new TaskWidget();
		taskWidget.AddKillRequestEventHandler(new Handler<TaskWidget>() {

			public void onFire(TaskWidget arg) {
				// TODO Auto-generated method stub
				RemoveTaskWidget(arg);
			}
		});
		taskWidgets.add(taskWidget);
		panelTaskList.add(taskWidget);
	}
	
	private void RemoveTaskWidget(TaskWidget taskWidget)
	{
		panelTaskList.remove(taskWidget);
		taskWidgets.remove(taskWidget);
		
		if (this.taskWidgets.isEmpty())
		{
			this.killRequestedEvent.dispatch("Empty");
		}
	}

	private TaskWidget getFirstTaskWidget()
	{
		return this.taskWidgets.get(0);
	}

	public int getTabIndex() {
		// TODO Auto-generated method stub
		return this.getFirstTaskWidget().getTabIndex();
	}

	public void setAccessKey(char key) {
		// TODO Auto-generated method stub
		this.getFirstTaskWidget().setAccessKey(key);
	}

	public void setTabIndex(int index) {
		// TODO Auto-generated method stub
		this.getFirstTaskWidget().setTabIndex(index);
	}

	public SimpleEvent<String> getKillRequestedEvent() {
		// TODO Auto-generated method stub
		return this.killRequestedEvent;
	}

	public void setFocus(boolean isFocused) {
		// TODO Auto-generated method stub
		this.getFirstTaskWidget().setFocus(isFocused);
	}



	@Override
	public TaskListData getData() {
		this.data.title = this.title.getText();
		return this.data;
	}


	@Override
	public void setData(TaskListData data) {
		this.title.setText(data.title);
		this.data = data;
	}



	@Override
	public void setElementData(ElementData data) {
		this.setData((TaskListData) data);
	}



}
