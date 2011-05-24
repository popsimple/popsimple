package com.project.canvas.client.canvastools.tasklist;

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
import com.project.canvas.client.canvastools.base.CanvasTool.ResizeMode;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.client.shared.events.SimpleEvent.Handler;
import com.project.canvas.shared.data.ElementData;
import com.project.canvas.shared.data.Point2D;
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

    protected int _tabIndex = 0;
    protected char _accessKey = 0;
    private SimpleEvent<String> killRequestedEvent = new SimpleEvent<String>();
    private ArrayList<TaskTool> taskWidgets = new ArrayList<TaskTool>();

    private TaskListData data = null;

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
        if (this.taskWidgets.isEmpty()) {
            return null;
        }
        return this.taskWidgets.get(0);
    }

    public int getTabIndex() {
        // TODO Auto-generated method stub
        return this._tabIndex;
    }

    public void setAccessKey(char key) {
        // TODO Auto-generated method stub
        this._accessKey = key;
    }

    public void setTabIndex(int index) {
        this._tabIndex = index;
    }

    public SimpleEvent<String> getKillRequestedEvent() {
        return this.killRequestedEvent;
    }

    @Override
    public void setFocus(boolean focused) {
        TaskTool taskTool = this.getFirstTaskWidget();
        if (null == taskTool) {
            return;
        }
        taskTool.setFocus(focused);
    }

    @Override
    public void setActive(boolean isActive) {
        this.setFocus(isActive);
    }

    @Override
    public TaskListData getValue() {
        this.data.title = this.title.getText();
        this.data.tasks.clear();
        for (TaskTool taskWidget : this.taskWidgets) {
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
        for (TaskData task : this.data.tasks) {
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

	@Override
	public ResizeMode getResizeMode() {
		return ResizeMode.BOTH;
	}


    @Override
	public HandlerRegistration addMoveEventHandler(Handler<Point2D> handler) {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public boolean canRotate() {
        return true;
    }
}
