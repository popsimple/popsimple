package com.tutorials.hellosomeone.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;


public class TaskWidget extends Composite {

	private static TaskWidgetUiBinder uiBinder = GWT
			.create(TaskWidgetUiBinder.class);

	interface TaskWidgetUiBinder extends UiBinder<Widget, TaskWidget> {
	}
	
	@UiField
	TextBox textTask;
	
	@UiField
	CheckBox checkTask;
	
	@UiField
	Image imageTask;
	
	@UiField
	Image imageRemove;
	
	private boolean isDefaultText = true;

	private ImageProvider imageProvider = new ImageProvider();
	
	final private HandlerManager handlerManager = new HandlerManager(this);
	
	public TaskWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		
		textTask.addFocusHandler(new FocusHandler() 
		{
			@Override
			public void onFocus(FocusEvent event) {
				// TODO Auto-generated method stub
				OnTaskTextFocus(event);
			}
		});
		
		textTask.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				// TODO Auto-generated method stub
				OnTextValueChanges(event);
			}
		});

		this.checkTask.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				// TODO Auto-generated method stub
				OnCheckChanged(event);
			}
		});
		
		final TaskWidget that = this;
		this.imageRemove.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				handlerManager.fireEvent(new TaskRemoveClickedEvent(that));
			}
		});
	}
	
	public void AddRemoveClickHandler(RemoveTaskClickedHandler handler)
	{
		handlerManager.addHandler(TaskRemoveClickedEvent.getType(), handler);
	}
	
	private void OnCheckChanged(ValueChangeEvent<Boolean> event) 
	{
		if (event.getValue())
		{
			this.textTask.addStyleName("TaskChecked");
			this.textTask.setReadOnly(true);
		}
		else
		{
			this.textTask.removeStyleName("TaskChecked");
			this.textTask.setReadOnly(false);
		}
	}
	
	private void OnTextValueChanges(ValueChangeEvent<String> event)
	{
		this.isDefaultText = false;
		
		this.imageTask.setUrl(this.imageProvider.GetImageUrl(event.getValue()));
	}
	
	private void OnTaskTextFocus(FocusEvent event)
	{
		if (false == this.isDefaultText)
		{
			return;
		}
		this.textTask.selectAll();
	}

}
