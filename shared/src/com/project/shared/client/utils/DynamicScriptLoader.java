package com.project.shared.client.utils;

import java.util.HashMap;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ScriptElement;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.events.SimpleEvent.Handler;

public class DynamicScriptLoader
{
    private final SimpleEvent.Handler<Void> handler;

    private DynamicScriptLoader(String source, SimpleEvent.Handler<Void> handler)
    {
        this.handler = handler;
        ScriptElement elem = Document.get().createScriptElement();
        elem.setSrc(source);
        elem.setLang("javascript");
        elem.setType("text/javascript");
        this.registerLoadedHandler(elem);
        Document.get().getElementsByTagName("head").getItem(0).appendChild(elem);
    }

    void scriptLoaded()
    {
        this.handler.onFire(null);
    }

    private native final void registerLoadedHandler(Element elem)
    /*-{
        var me = this;
        elem.onload = function() {
            me.@com.project.shared.client.utils.DynamicScriptLoader::scriptLoaded()();
        };
    }-*/;

    private final static HashMap<String, Boolean> scriptLoadStatusMap = new HashMap<String, Boolean>();
    private final static HashMap<String, SimpleEvent<Void>> scriptLoadHandlersMap = new HashMap<String, SimpleEvent<Void>>();

    public static void Load(final String source, final SimpleEvent.Handler<Void> handler)
    {
        Boolean status = scriptLoadStatusMap.get(source);
        if (null == status) {
            // Do the script-element creation and handling,
            // but start the process in a deferred command
            // so it doesn't stop the page from loading if it hasn't finished yet.
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute()
                {
                    loadSource(source, handler);
                }
            });
            return;
        }

        if (false == status) {
            // not the first time someone wants to load this source
            // but it didn't load yet.
            scriptLoadHandlersMap.get(source).addHandler(handler);
            return;
        }

        // Already loaded. Fire the handler in a deferred scheduler.
        // (for consistently async behavior)
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute()
            {
                handler.onFire(null);
            }
        });
    }

    private static void loadSource(final String source, final SimpleEvent.Handler<Void> handler)
    {
        // no entry exists for this source -
        // first load request
        scriptLoadStatusMap.put(source, false);
        SimpleEvent<Void> event = new SimpleEvent<Void>();
        scriptLoadHandlersMap.put(source, event);
        event.addHandler(handler);

        Handler<Void> wrappedHandler = new SimpleEvent.Handler<Void>() {
            @Override
            public void onFire(Void arg)
            {
                sourceLoaded(source);
            }
        };
        new DynamicScriptLoader(source, wrappedHandler);
    }

    private static void sourceLoaded(final String source)
    {
        scriptLoadStatusMap.put(source, true);
        SimpleEvent<Void> prevEvent = scriptLoadHandlersMap.get(source);
        scriptLoadHandlersMap.remove(source);
        prevEvent.dispatch(null);
    }
}
