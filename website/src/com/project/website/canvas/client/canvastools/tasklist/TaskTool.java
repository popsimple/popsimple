package com.project.website.canvas.client.canvastools.tasklist;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.utils.NativeUtils;
import com.project.shared.client.utils.StyleUtils;
import com.project.website.canvas.client.resources.CanvasResources;
import com.project.website.canvas.shared.data.TaskData;

public class TaskTool extends Composite implements Focusable, TakesValue<TaskData> {

    private static TaskToolUiBinder uiBinder = GWT.create(TaskToolUiBinder.class);

    interface TaskToolUiBinder extends UiBinder<Widget, TaskTool> {
    }

    @UiField
    HoverTextBox textTask;

    @UiField
    CheckBox checkTask;

    @UiField
    Button imageRemove;

    @UiField
    FlowPanel imageTask;

    private ImageProvider imageProvider = new ImageProvider();

    private final SimpleEvent<TaskTool> killRequestEvent = new SimpleEvent<TaskTool>();

    private TaskData data = new TaskData();

    public TaskTool() {
        initWidget(uiBinder.createAndBindUi(this));

        this.registerHandlers();

        this.setImageUrl(ImageProvider.getDefaultImageUrl());
    }

    private void registerHandlers()
    {
        this.checkTask.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

            public void onValueChange(ValueChangeEvent<Boolean> arg0) {
                OnCheckChanged(arg0);
            }
        });

        this.textTask.addKeyPressHandler(new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent event) {
                if (false == NativeUtils.keyIsSpace(event))
                {
                    return;
                }
                if (false == textTask.isReadOnly())
                {
                    return;
                }
                checkTask.setValue(false, true);
                event.preventDefault();
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
                killRequestEvent.dispatch(that);
            }
        });

        this.textTask.addFocusHandler(new FocusHandler() {

            public void onFocus(FocusEvent event) {
                // TODO Auto-generated method stub
                textTask.selectAll();
            }
        });

    }

    private void setImageUrl(String url) {
        this.imageTask.getElement().getStyle().setBackgroundImage(
                StyleUtils.buildBackgroundUrl(url));
    }

    public HandlerRegistration addKillRequestEventHandler(SimpleEvent.Handler<TaskTool> handler) {
        return this.killRequestEvent.addHandler(handler);
    }

    private void OnCheckChanged(ValueChangeEvent<Boolean> event) {
        boolean checked = event.getValue();
        setCompleted(checked);
    }

    private void textValueChanges(String text) {
        this.setImageUrl(imageProvider.getImageUrl(text));
    }

    private void setCompleted(boolean checked) {
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
        this.textTask.setText(value.description);
        this.checkTask.setValue(value.completed);
        this.setImageUrl(value.imageUrl);
        // TODO: Support image alternate text
        this.setCompleted(value.completed);
    }

    @Override
    public TaskData getValue() {
        this.data.description = this.textTask.getText();
        this.data.completed = this.checkTask.getValue();
        this.data.imageUrl = StyleUtils.getBackgroundUrl(this.imageTask.getElement().getStyle());
        // TODO: Support image alternate text
        return this.data;
    }
}
