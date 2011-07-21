package com.project.shared.client.utils;

import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.data.funcs.Func;
import com.project.shared.data.funcs.Func.Action;

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

    public static <T> Handler<T> chain(final Handler<T> a, final Handler<T> b) {
        return new Handler<T>(){
            @Override
            public void onFire(T arg)
            {
                a.onFire(arg);
                b.onFire(arg);
            }};
    }

    public static <A,B> Handler<A> fromFunc(final Func<A, B> func) {
        return new Handler<A>(){
            @Override
            public void onFire(A arg)
            {
                func.call(arg);
            }
        };
    }

    public static <A> Func<A, Void> toFunc(final Handler<A> handler) {
        return new Action<A>() {
            @Override
            public void exec(A arg)
            {
                handler.onFire(arg);
            }
        };
    }
}
