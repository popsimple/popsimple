package com.project.shared.client.utils;

import com.google.common.base.Objects;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.data.Point2D;
import com.project.shared.utils.loggers.Logger;

public class EventUtils
{
    public static <H extends EventHandler> boolean nativePreviewEventTypeIsAny(NativePreviewEvent nativePreviewEvent, DomEvent.Type<?>... domEventTypes)
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
        return (Objects.equal(nativePreviewEvent.getNativeEvent().getType(), domEventType.getName()));
    }

    public static Point2D getCurrentMousePos()
    {
        Event currentEvent = Event.getCurrentEvent();
        if (null == currentEvent){
            return null;
        }
        if (EventUtils.hasTouches(currentEvent)) {
            Logger.info(ElementUtils.class, "mouse event has touches: " + String.valueOf(currentEvent.getTouches().length()));
            if (0 < currentEvent.getTouches().length()) {
            	Touch touch = currentEvent.getTouches().get(0);
            	Logger.info(ElementUtils.class, "touch: " + touch.getClientX());
            	Logger.info(ElementUtils.class, touch.toString());
            	return new Point2D(touch.getClientX(), touch.getClientY());
            }
        }
        return new Point2D(currentEvent.getClientX(), currentEvent.getClientY());
    }

    public static <H extends EventHandler> HandlerRegistration addNativePreviewEvent(
            final DomEvent.Type<H> domEventType, final Handler<NativePreviewEvent> handler)
    {
        return Event.addNativePreviewHandler(new NativePreviewHandler() {
            @Override public void onPreviewNativeEvent(NativePreviewEvent event) {
                NativeEvent nativeEvent = null == event ? null : event.getNativeEvent();
                if (null == nativeEvent) {
                    return;
                }
                if (false == EventUtils.nativePreviewEventTypeEquals(event, domEventType))
                {
                    return;
                }
                handler.onFire(event);
            }});
    }

    public static native final boolean hasTouches(NativeEvent evt) /*-{
        return (undefined !== evt.touches);
    }-*/;

}
