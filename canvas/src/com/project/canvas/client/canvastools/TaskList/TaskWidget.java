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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.project.canvas.client.resources.CanvasResources;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.shared.data.Task;


public class TaskWidget extends Composite implements Focusable, TakesValue<Task> {

	private static TaskWidgetUiBinder uiBinder = GWT
			.create(TaskWidgetUiBinder.class);

	interface TaskWidgetUiBinder extends UiBinder<Widget, TaskWidget> {
	}
	
	@UiField
	HoverTextBox textTask;
	
	@UiField
	CheckBox checkTask;
	
	@UiField
	Button imageRemove;
	
	@UiField
	Image imageTask;
	
	private ImageProvider imageProvider = new ImageProvider();
	
	private final SimpleEvent<TaskWidget> killRequestEvent = new SimpleEvent<TaskWidget>();

	private Task data = new Task();
	
	public TaskWidget() {
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

		final TaskWidget that = this;
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
		
		this.imageTask.setUrl(imageProvider.GetDefaultImageUrl());
	}
	
	public void AddKillRequestEventHandler(SimpleEvent.Handler<TaskWidget> handler)
	{
		this.killRequestEvent.addHandler(handler);
	}
	
	private void OnCheckChanged(ValueChangeEvent<Boolean> event) 
	{
		boolean checked = event.getValue();
		setCompleted(checked);
	}

	protected void textValueChanges(String text)
	{
		imageTask.setUrl(imageProvider.GetImageUrl(text));
	}
	
	protected void setCompleted(boolean checked) {
		if (checked)
		{
			this.textTask.addStyleName(CanvasResources.INSTANCE.main().taskListTextChecked());
			this.imageTask.addStyleName(CanvasResources.INSTANCE.main().taskImageChecked());
			this.textTask.setReadOnly(true);
		}
		else
		{
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
	public void setValue(Task value) {
		this.data = value;
		this.textTask.setText(value.description);
		this.checkTask.setValue(value.completed);
		this.setCompleted(value.completed);
		this.textValueChanges(textTask.getText());
	}

	@Override
	public Task getValue() {
		this.data.description = this.textTask.getText();
		this.data.completed = this.checkTask.getValue();
		return this.data;
	}

}
