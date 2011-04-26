package com.tutorials.hellosomeone.client;

import com.google.gwt.event.shared.GwtEvent;

public class TaskRemoveClickedEvent extends GwtEvent<RemoveTaskClickedHandler>
{
	private static final Type<RemoveTaskClickedHandler> TYPE = new Type<RemoveTaskClickedHandler>();
	
	private final TaskWidget taskWidget;
	
	public TaskRemoveClickedEvent(TaskWidget taskWidget)
	{
		this.taskWidget = taskWidget;
	}
	
	@Override
	public Type<RemoveTaskClickedHandler> getAssociatedType() {
		// TODO Auto-generated method stub
		return TYPE;
	}
	
	public static Type<RemoveTaskClickedHandler> getType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(RemoveTaskClickedHandler handler) 
	{
		handler.onTaskRemoveClicked(this);		
	}
	
	public TaskWidget getTaskWidget()
	{
		return this.taskWidget;
	}
}
