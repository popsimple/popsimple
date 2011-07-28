package com.project.shared.client.utils;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.data.Point2D;

public class EventUtils
{
    public static Point2D getCurrentMousePos()
    {
        Event currentEvent = Event.getCurrentEvent();
        if (null == currentEvent){
            return null;
        }
        return new Point2D(currentEvent.getClientX(), currentEvent.getClientY());
    }

	public static ClickHandler asClickHandler(final Handler<Void> handler) {
		return new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				handler.onFire(null);
			}};
	}
}
