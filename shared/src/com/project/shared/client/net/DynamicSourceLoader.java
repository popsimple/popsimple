package com.project.shared.client.net;

import java.util.HashMap;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.dom.client.ScriptElement;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.client.events.SingleEvent;
import com.project.shared.client.utils.HandlerUtils;
import com.project.shared.data.funcs.AsyncFunc;
import com.project.shared.data.funcs.Func;
import com.project.shared.utils.loggers.Logger;

public class DynamicSourceLoader
{
    private final SimpleEvent.Handler<Void> handler;

    private DynamicSourceLoader(String source, SimpleEvent.Handler<Void> handler)
    {
        this.handler = handler;
        String normalizedSource = source.toLowerCase().trim();
        Element elem = null;
        // TODO: replace the endsWith heuristic with a parameter that tells what type of element to create or a function that creates the proper element
        if (normalizedSource.endsWith(".css"))
        {
            LinkElement linkElem = Document.get().createLinkElement();
            linkElem.setHref(source);
            linkElem.setRel("stylesheet");
            linkElem.setType("text/css");
            elem = linkElem;
        }
        else {
            // If it isn't css, assume it's javascript (sometimes javascript urls don't end with .js because they are queries that dynamically generate js)
            ScriptElement scriptElem = Document.get().createScriptElement();
            scriptElem.setSrc(source);
            // scriptElem.setLang("javascript"); // lang is deprecated?
            scriptElem.setType("text/javascript");
            elem = scriptElem;
        }
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
			me.@com.project.shared.client.net.DynamicSourceLoader::scriptLoaded()();
		};
    }-*/;

    private final static HashMap<String, Boolean> scriptLoadStatusMap = new HashMap<String, Boolean>();
    private final static HashMap<String, SingleEvent<Void>> scriptLoadHandlersMap = new HashMap<String, SingleEvent<Void>>();

    public static void load(final String source, final SimpleEvent.Handler<Void> handler)
    {
        getLoadAsyncFunc(source).then(HandlerUtils.toFunc(handler))
                                .run(null);
    }

    public static AsyncFunc<Void, Void> getLoadAsyncFunc(final String source)
    {
        return new AsyncFunc<Void, Void>() {
            @Override
            protected <S, E> void run(Void arg, final Func<Void, S> successHandler, final Func<Throwable, E> errorHandler)
            {
                Logger.log("actionLoad starting: " + source);

                final Handler<Void> innerHandler = HandlerUtils.fromFunc(successHandler);

                Boolean status = scriptLoadStatusMap.get(source);
                if (null == status) {
                    scriptLoadStatusMap.put(source, false);
                    scriptLoadHandlersMap.put(source, new SingleEvent<Void>());

                    // Do the script-element creation and handling,
                    // but start the process in a deferred command
                    // so it doesn't stop the page from loading if it hasn't
                    // finished yet.
                    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                        @Override
                        public void execute()
                        {
                            loadSource(source, innerHandler);
                        }
                    });
                    return;
                }

                if (false == status) {
                    // not the first time someone wants to load this source
                    // but it didn't load yet.
                    scriptLoadHandlersMap.get(source).addHandler(innerHandler);
                    return;
                }

                // Already loaded. Fire the handler in a deferred scheduler.
                // (for consistently async behavior)
                HandlerUtils.fireDeferred(HandlerUtils.fromFunc(successHandler), null);
            }
        };
    }

    private static void loadSource(final String source, final SimpleEvent.Handler<Void> handler)
    {
        Logger.log("Loading source: " + source);
        // no entry exists for this source -
        // first load request
        SingleEvent<Void> event = scriptLoadHandlersMap.get(source);
        event.addHandler(handler);

        Handler<Void> wrappedHandler = new SimpleEvent.Handler<Void>() {
            @Override
            public void onFire(Void arg)
            {
                sourceLoaded(source);
            }
        };
        new DynamicSourceLoader(source, wrappedHandler);
    }

    private static void sourceLoaded(final String source)
    {
        Logger.log("Finished loading: " + source);
        scriptLoadStatusMap.put(source, true);
        scriptLoadHandlersMap.get(source).dispatch(null);
    }
}
