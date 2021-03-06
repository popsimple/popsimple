package com.project.website.canvas.client.canvastools.tasklist;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.client.handlers.RegistrationsManager;
import com.project.shared.client.handlers.SpecificKeyPressHandler;
import com.project.shared.client.utils.ListUtils;
import com.project.website.canvas.client.canvastools.base.CanvasToolCommon;
import com.project.website.canvas.client.canvastools.base.CanvasToolEvents;
import com.project.website.canvas.client.canvastools.base.ResizeMode;
import com.project.website.canvas.client.canvastools.base.interfaces.CanvasTool;
import com.project.website.canvas.client.canvastools.base.interfaces.ICanvasToolEvents;
import com.project.website.canvas.shared.data.ElementData;
import com.project.website.canvas.shared.data.TaskData;
import com.project.website.canvas.shared.data.TaskListData;

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

    private CanvasToolEvents _toolEvents = new CanvasToolEvents(this);

    protected int _tabIndex = 0;
    protected char _accessKey = 0;
    private ArrayList<TaskTool> taskWidgets = new ArrayList<TaskTool>();

    private TaskListData data = null;

    public TaskListTool() {
        initWidget(uiBinder.createAndBindUi(this));
        CanvasToolCommon.initCanvasToolWidget(this);

        this.createNewTaskWidget();
    }

    @Override
    public ICanvasToolEvents getToolEvents()
    {
        return this._toolEvents;
    }

    @Override
    public void bind() {
        this.registerHandlers();
    }

    private void registerHandlers()
    {
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

    public void addTaskWidget(final TaskTool taskWidget) {
        taskWidgets.add(taskWidget);
        panelTaskList.add(taskWidget);

        final RegistrationsManager taskRegistrations = new RegistrationsManager();

        taskRegistrations.add(taskWidget.addKillRequestEventHandler(new Handler<TaskTool>() {

            public void onFire(TaskTool arg) {
                removeTaskWidget(arg);
                taskRegistrations.clear();
            }
        }));

        taskRegistrations.add(taskWidget.textTask.addKeyPressHandler(
                new SpecificKeyPressHandler(KeyCodes.KEY_ENTER) {
            @Override
            public void onSpecificKeyPress(KeyPressEvent event) {
                    activateTask(ListUtils.getNext(taskWidgets, taskWidget, true));
            }
        }));

        taskRegistrations.add(taskWidget.textTask.addKeyPressHandler(
                new SpecificKeyPressHandler(KeyCodes.KEY_DOWN) {
            @Override
            public void onSpecificKeyPress(KeyPressEvent event) {
                activateTask(ListUtils.getNext(taskWidgets, taskWidget, true));
            }
        }));

        taskRegistrations.add(taskWidget.textTask.addKeyPressHandler(
                new SpecificKeyPressHandler(KeyCodes.KEY_UP) {
            @Override
            public void onSpecificKeyPress(KeyPressEvent event) {
                activateTask(ListUtils.getPrevious(taskWidgets, taskWidget, true));
            }
        }));
    }

    private void activateTask(TaskTool taskTool)
    {
        taskTool.setFocus(true);
    }

    private void removeTaskWidget(TaskTool taskWidget) {
        panelTaskList.remove(taskWidget);
        taskWidgets.remove(taskWidget);
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
	public ResizeMode getResizeMode() {
		return ResizeMode.WIDTH_ONLY;
	}

    @Override
    public boolean canRotate() {
        return true;
    }

    @Override
    public void setViewMode(boolean isViewMode)
    {
        // TODO How does a task list behave when in view mode? for now same as edit mode
    }

    @Override
    public void onResize() {
        // TODO Auto-generated method stub
    }

    @Override
    public IsWidget getToolbar()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
