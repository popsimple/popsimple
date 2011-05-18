package com.project.canvas.client.canvastools.TaskList;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.HandlerRegistration;
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
import com.project.canvas.shared.data.TaskData;
import com.project.canvas.shared.data.TaskListData;

public class TaskListTool extends Composite implements CanvasTool<TaskListData>, Focusable {

    private static TaskListWidgetUiBinder uiBinder = GWT.create(TaskListWidgetUiBinder.class);

    interface TaskListWidgetUiBinder extends UiBinder<Widget, TaskListTool> {
    }

    @UiField
    HTMLPanel panelTaskList;

    @UiField
    HoverTextBox title;

    @UiField
    Button buttonAdd;

    private SimpleEvent<String> killRequestedEvent = new SimpleEvent<String>();
    private ArrayList<TaskTool> taskWidgets = new ArrayList<TaskTool>();

    private TaskListData data = new TaskListData();

    public TaskListTool() {
        initWidget(uiBinder.createAndBindUi(this));
        CanvasToolCommon.initCanvasToolWidget(this);

        this.createNewTaskWidget();
    }

    @Override
    public void bind() {
        buttonAdd.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                // TODO Auto-generated method stub
                createNewTaskWidget();
            }
        });
    }

    private void createNewTaskWidget() {
        TaskTool taskWidget = new TaskTool();
        addTaskWidget(taskWidget);
        taskWidget.setFocus(true);
    }

    public void addTaskWidget(TaskTool taskWidget) {
        taskWidget.AddKillRequestEventHandler(new Handler<TaskTool>() {

            public void onFire(TaskTool arg) {
                // TODO Auto-generated method stub
                removeTaskWidget(arg);
            }
        });
        taskWidgets.add(taskWidget);
        panelTaskList.add(taskWidget);
    }

    private void removeTaskWidget(TaskTool taskWidget) {
        panelTaskList.remove(taskWidget);
        taskWidgets.remove(taskWidget);

        if (this.taskWidgets.isEmpty()) {
            this.killRequestedEvent.dispatch("Empty");
        }
    }

    private TaskTool getFirstTaskWidget() {
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

    @Override
    public void setFocus(boolean focused) {
        this.getFirstTaskWidget().setFocus(focused);
    }

    @Override
    public void setActive(boolean isActive) {
        this.setFocus(isActive);
    }

    @Override
    public TaskListData getValue() {
        this.data._title = this.title.getText();
        this.data._tasks.clear();
        for (TaskTool taskWidget : this.taskWidgets) {
            this.data._tasks.add(taskWidget.getValue());
        }
        return this.data;
    }

    @Override
    public void setValue(TaskListData data) {
        this.title.setText(data._title);
        this.data = data;
        this.taskWidgets.clear();
        this.panelTaskList.clear();
        for (TaskData task : this.data._tasks) {
            TaskTool taskWidget = new TaskTool();
            taskWidget.setValue(task);
            this.addTaskWidget(taskWidget);
        }
    }

    @Override
    public void setElementData(ElementData data) {
        this.setValue((TaskListData) data);
    }

    @Override
    public HandlerRegistration addMoveStartEventHandler(Handler<MouseEvent<?>> handler) {
        return null;
    }
}
