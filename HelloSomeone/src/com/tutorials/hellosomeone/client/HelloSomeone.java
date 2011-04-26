package com.tutorials.hellosomeone.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class HelloSomeone implements EntryPoint {
	/**
	 * This is the entry point method.
	 */
	
	MainWidget mainWidget = new MainWidget();
	
	public void onModuleLoad() 
	{
		RootPanel.get("root").add(mainWidget);
	}
}
