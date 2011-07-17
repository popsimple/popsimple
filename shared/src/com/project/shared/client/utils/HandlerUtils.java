package com.project.shared.client.utils;

import com.project.shared.client.events.SimpleEvent.Handler;

public class HandlerUtils {
    private static final Handler<Object> EMPTY_HANDLER = new Handler<Object>() {
        @Override
        public void onFire(Object arg) {
        }
    };

    @SuppressWarnings("unchecked")
    public static final <T> Handler<T> emptyHandler()
    {
        return (Handler<T>)HandlerUtils.EMPTY_HANDLER;
    }
}
