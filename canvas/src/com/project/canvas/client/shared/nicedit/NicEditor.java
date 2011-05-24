package com.project.canvas.client.shared.nicedit;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class NicEditor
{

    private static final String[] buttonsList = { "bold", "italic", "underline", "forecolor", "bgcolor", "left",
            "center", "right", "justify", "ol", "ul", "fontSize", "fontFamily", "indent", "outdent", "link", "unlink" };
    private static int id = 0;
    private static boolean inited = false;
    private static AsyncCallback<Void> loadCallback;

    private JavaScriptObject nativeNicEditor;
    private Widget widget;

    public NicEditor(Widget widget, final AsyncCallback<Void> ready)
    {
        this.widget = widget;
        final Element element = widget.getElement();
        NicEditor.staticInit(new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught)
            {
                ready.onFailure(caught);
            }

            @Override
            public void onSuccess(Void result)
            {
                replaceElement(element);
                ready.onSuccess(result);
            }
        });
    }

    public HandlerRegistration addBlurHandler(BlurHandler handler)
    {
        return this.widget.addDomHandler(handler, BlurEvent.getType());
    }

    public HandlerRegistration addKeyDownHandler(KeyDownHandler handler)
    {
        return this.widget.addDomHandler(handler, KeyDownEvent.getType());
    }

    public String getContent()
    {
        return NicEditor.getContent(nativeNicEditor);
    }

    public Element getEditorElement()
    {
        return NicEditor.getEditorElement(nativeNicEditor);
    }

    public void replaceElement(Element element)
    {
        String elemId = element.getId();
        if (elemId.isEmpty()) {
            // TODO make this random and unique.
            elemId = this.getClass().getName() + "_" + NicEditor.id;
            element.setId(elemId);
            NicEditor.id++;
        }
        JsArrayString jsButtonsList = (JsArrayString) JsArrayString.createArray();
        for (String buttonName : buttonsList) {
            jsButtonsList.push(buttonName);
        }
        nativeNicEditor = nativeCreateNicEdit(elemId, jsButtonsList);
    }

    public void setContent(String content)
    {
        NicEditor.setContent(nativeNicEditor, content);
    }

    private void dispatchBlur()
    {
        BlurEvent.fireNativeEvent(Document.get().createBlurEvent(), this.widget);
    }

    private void dispatchKeyDown(NativeEvent event)
    {
        // TODO: Find a way to pass both CharCode and KeyCode to the
        // KeyDownEvent
        NativeEvent keyDownEvent = Document.get().createKeyDownEvent(event.getCtrlKey(), event.getAltKey(),
                event.getShiftKey(), event.getMetaKey(), event.getKeyCode());
        KeyDownEvent.fireNativeEvent(keyDownEvent, this.widget);
    }

    private native final JavaScriptObject nativeCreateNicEdit(String id, JsArrayString buttonNames)
    /*-{
		var me = this;
		var e = $wnd.nicEditor;
		var inst = new e({buttonList: buttonNames});
		var res = inst.panelInstance(id);
		var nicInstance = res.nicInstances[0];
		res.addEvent('blur', function() {
			me.@com.project.canvas.client.shared.nicedit.NicEditor::dispatchBlur()();
		});
		res.addEvent('key', function(src, ev) {
    		me.@com.project.canvas.client.shared.nicedit.NicEditor::dispatchKeyDown(Lcom/google/gwt/dom/client/NativeEvent;)(ev);
    	});
		return nicInstance;
    }-*/;

    private native static final String getContent(JavaScriptObject nicInstance) /*-{
		return nicInstance.getContent();
    }-*/;

    private native static final Element getEditorElement(JavaScriptObject nicInstance) /*-{
		return nicInstance.getElm();
    }-*/;

    private static final void scriptArrived()
    {
        loadCallback.onSuccess(null);
    }

    private static final native void setContent(JavaScriptObject nicInstance, String content)
    /*-{
		return nicInstance.setContent(content);
    }-*/;

    private static void staticInit(final AsyncCallback<Void> callback)
    {
        if (inited) {
            // Already inited, call the callback - but not immediately,
            // because the caller may be expecting us to return before the
            // callback is called.
            // (staticInit is used in the constructor of NicEditor)
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute()
                {
                    callback.onSuccess(null);
                }
            });
            return;
        }
        inited = true;
        ScriptElement elem = Document.get().createScriptElement();
        elem.setSrc("nicEdit/nicEdit.js");
        elem.setLang("javascript");
        elem.setType("text/javascript");
        waitForElem(elem);
        Document.get().getElementsByTagName("head").getItem(0).appendChild(elem);
        // TODO chain more than one callback
        loadCallback = callback;
    }

    private static native final void waitForElem(Element elem)
    /*-{
        elem.onload = function() {
            @com.project.canvas.client.shared.nicedit.NicEditor::scriptArrived()();
        };
    }-*/;
}
