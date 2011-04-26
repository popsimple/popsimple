package com.tutorials.hellosomeone.client;

import com.google.gwt.event.shared.EventHandler;

public interface RemoveTaskClickedHandler extends EventHandler 
{
	void onTaskRemoveClicked(TaskRemoveClickedEvent event);
}
