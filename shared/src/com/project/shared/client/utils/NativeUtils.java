package com.project.shared.client.utils;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.project.shared.data.funcs.Func;

public class NativeUtils
{

    public static boolean keyIsEnter(KeyPressEvent event)
    {
        return event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER;
    }

    /**
     * Untested
     */
    public static class JavascriptFunctionWrapper<B> {
        private Func<Object, B> func;

        public JavascriptFunctionWrapper(Func<Object, B> func) {
            this.func = func;
        }

        public B call(Object arg)
        {
            return this.func.call(arg);
        }

        public final native JavaScriptObject asJavaScriptObject() /*-{
            var me = this;
            return me.@com.project.shared.client.utils.NativeUtils.JavascriptFunctionWrapper::call(Ljava/lang/Object;);
        }-*/;
    }

    /**
     * Untested
     */
    public static <B> JavaScriptObject asJavascriptFunction(Func<Object,B> func)
    {
        return (new JavascriptFunctionWrapper<B>(func)).asJavaScriptObject();
    }
}
