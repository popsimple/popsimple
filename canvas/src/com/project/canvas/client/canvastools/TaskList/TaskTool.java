package com.project.canvas.client.canvastools.TaskList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Widget;
import com.project.canvas.client.resources.CanvasResources;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.shared.data.TaskData;

public class TaskTool extends Composite implements Focusable, TakesValue<TaskData> {

    private static TaskToolUiBinder uiBinder = GWT.create(TaskToolUiBinder.class);

    interface TaskToolUiBinder extends UiBinder<Widget, TaskTool> {
    }

    @UiField
    HoverTextBox textTask;

    @UiField
    CheckBox checkTask;

    @UiField
    Anchor imageRemove;

    @UiField
    FlowPanel imageTask;

    private ImageProvider imageProvider = new ImageProvider();

    private final SimpleEvent<TaskTool> killRequestEvent = new SimpleEvent<TaskTool>();

    private TaskData data = new TaskData();

    public TaskTool() {
        initWidget(uiBinder.createAndBindUi(this));

        this.checkTask.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

            public void onValueChange(ValueChangeEvent<Boolean> arg0) {
                // TODO Auto-generated method stub
                OnCheckChanged(arg0);
            }
        });

        this.textTask.addValueChangeHandler(new ValueChangeHandler<String>() {

            public void onValueChange(ValueChangeEvent<String> event) {
                textValueChanges(event.getValue());
            }
        });

        final TaskTool that = this;
        this.imageRemove.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                // TODO Auto-generated method stub
                killRequestEvent.dispatch(that);
            }
        });

        this.textTask.addFocusHandler(new FocusHandler() {

            public void onFocus(FocusEvent event) {
                // TODO Auto-generated method stub
                textTask.selectAll();
            }
        });

        this.setImageUrl(imageProvider.GetDefaultImageUrl());
    }

    // TODO: Share with ImageTool.
    protected void setImageUrl(String url) {
        this.imageTask.getElement().getStyle().setBackgroundImage("url(\"" + url + "\")");
    }

    public void AddKillRequestEventHandler(SimpleEvent.Handler<TaskTool> handler) {
        this.killRequestEvent.addHandler(handler);
    }

    private void OnCheckChanged(ValueChangeEvent<Boolean> event) {
        boolean checked = event.getValue();
        setCompleted(checked);
    }

    protected void textValueChanges(String text) {
        this.setImageUrl(imageProvider.GetImageUrl(text));
    }

    protected void setCompleted(boolean checked) {
        if (checked) {
            this.textTask.addStyleName(CanvasResources.INSTANCE.main().taskListTextChecked());
            this.imageTask.addStyleName(CanvasResources.INSTANCE.main().taskImageChecked());
            this.textTask.setReadOnly(true);
        } else {
            this.textTask.removeStyleName(CanvasResources.INSTANCE.main().taskListTextChecked());
            this.imageTask.removeStyleName(CanvasResources.INSTANCE.main().taskImageChecked());
            this.textTask.setReadOnly(false);
        }
    }

    public int getTabIndex() {
        // TODO Auto-generated method stub
        return this.textTask.getTabIndex();
    }

    public void setAccessKey(char key) {
        // TODO Auto-generated method stub
        this.textTask.setAccessKey(key);
    }

    public void setFocus(boolean focused) {
        // TODO Auto-generated method stub
        this.textTask.setFocus(focused);
    }

    public void setTabIndex(int index) {
        // TODO Auto-generated method stub
        this.textTask.setTabIndex(index);
    }

    @Override
    public void setValue(TaskData value) {
        this.data = value;
        this.textTask.setText(value._description);
        this.checkTask.setValue(value._completed);
        this.setImageUrl(value._imageUrl);
        // TODO: Support image alternate text
        this.setCompleted(value._completed);
    }

    @Override
    public TaskData getValue() {
        this.data._description = this.textTask.getText();
        this.data._completed = this.checkTask.getValue();
        this.data._imageUrl = this.getImageUrl();
        // TODO: Support image alternate text
        return this.data;
    }

    // TODO: Share with ImageTool.
    protected String getImageUrl() {
        String imageCss = this.imageTask.getElement().getStyle().getBackgroundImage();
        if (imageCss.contains("url(")) {
            return imageCss.substring("url(\"".length(), imageCss.length() - "\")".length());
        } else {
            return "";
        }
    }

}
