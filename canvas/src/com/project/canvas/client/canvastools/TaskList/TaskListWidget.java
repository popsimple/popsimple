package com.project.canvas.client.canvastools.TaskList;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.project.canvas.client.canvastools.base.CanvasTool;
import com.project.canvas.client.canvastools.base.CanvasToolCommon;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.client.shared.events.SimpleEvent.Handler;
import com.project.canvas.shared.data.ElementData;
import com.project.canvas.shared.data.Task;
import com.project.canvas.shared.data.TaskListData;

public class TaskListWidget extends Composite implements CanvasTool<TaskListData>, Focusable {

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

	private TaskListData data = new TaskListData();
	
	public TaskListWidget() 
	{
		initWidget(uiBinder.createAndBindUi(this));
		CanvasToolCommon.initCanvasToolWidget(this);
		
		this.createNewTaskWidget();
		
		buttonAdd.addClickHandler(new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				createNewTaskWidget();
			}
		});
	}


	private void createNewTaskWidget()
	{
		TaskWidget taskWidget = new TaskWidget();
		addTaskWidget(taskWidget);
		taskWidget.setFocus(true);
	}


	public void addTaskWidget(TaskWidget taskWidget) {
		taskWidget.AddKillRequestEventHandler(new Handler<TaskWidget>() {

			public void onFire(TaskWidget arg) {
				// TODO Auto-generated method stub
				removeTaskWidget(arg);
			}
		});
		taskWidgets.add(taskWidget);
		panelTaskList.add(taskWidget);
	}
	
	private void removeTaskWidget(TaskWidget taskWidget)
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
		this.getFirstTaskWidget().setTabIndex(index);
	}

	public SimpleEvent<String> getKillRequestedEvent() {
		return this.killRequestedEvent;
	}

	public void setFocus(boolean isFocused) {
		this.getFirstTaskWidget().setFocus(isFocused);
	}

	@Override
	public TaskListData getValue() {
		this.data.title = this.title.getText();
		this.data.tasks.clear();
		for (TaskWidget taskWidget : this.taskWidgets) {
			this.data.tasks.add(taskWidget.getValue());
		}
		return this.data;
	}

	@Override
	public void setValue(TaskListData data) {
		this.title.setText(data.title);
		this.data = data;
		this.taskWidgets.clear();
		this.panelTaskList.clear();
		for (Task task : this.data.tasks) {
			TaskWidget taskWidget = new TaskWidget();
			taskWidget.setValue(task);
			this.addTaskWidget(taskWidget);
		}
	}

	@Override
	public void setElementData(ElementData data) {
		this.setValue((TaskListData) data);
	}
}
