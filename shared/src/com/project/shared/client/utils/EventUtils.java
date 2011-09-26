package com.project.shared.client.utils;

import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.project.shared.data.Point2D;
import com.project.shared.utils.ObjectUtils;

public class EventUtils
{
    public static <H extends EventHandler> boolean nativePreviewEventTypeIsAny(NativePreviewEvent nativePreviewEvent, DomEvent.Type<?>[] domEventTypes)
    {
        for (DomEvent.Type<?> domEventType : domEventTypes)
        {
            if (nativePreviewEventTypeEquals(nativePreviewEvent, domEventType)) {
                return true;
            }
        }
        return false;
    }

    public static <H extends EventHandler> boolean nativePreviewEventTypeEquals(NativePreviewEvent nativePreviewEvent, DomEvent.Type<H> domEventType)
    {
        if ((null == nativePreviewEvent) || (null == nativePreviewEvent.getNativeEvent()) || (null == nativePreviewEvent.getNativeEvent().getType()))
        {
            return false;
        }
        return (ObjectUtils.areEqual(nativePreviewEvent.getNativeEvent().getType(), domEventType.getName()));
    }

    public static Point2D getCurrentMousePos()
    {
        Event currentEvent = Event.getCurrentEvent();
        if (null == currentEvent){
            return null;
        }
        return new Point2D(currentEvent.getClientX(), currentEvent.getClientY());
    }
}
