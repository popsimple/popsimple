package com.project.canvas.shared;

import com.google.gwt.user.client.Event;
import com.project.canvas.shared.data.Point2D;

public class EventUtils
{
    public static Point2D getCurrentLocation()
    {
        Event currentEvent = Event.getCurrentEvent();
        if (null == currentEvent){
            return null;
        }
        return new Point2D(currentEvent.getClientX(), currentEvent.getClientY());
    }
}
