package com.project.shared.utils;

import com.google.gwt.user.client.Event;
import com.project.shared.data.Point2D;

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
