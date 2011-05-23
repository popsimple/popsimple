package com.project.canvas.client.shared.nicedit;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class NicEditor
{
    private static int id = 0;
    private JavaScriptObject nativeNicEditor;

    public NicEditor(final Element element) {
        NicEditor.staticInit(new AsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result)
            {
                replaceElement(element);
            }
            
            @Override
            public void onFailure(Throwable caught)
            {
                // TODO Auto-generated method stub
                
            }
        });
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
        nativeNicEditor = NicEditor.nativeCreateNicEdit(elemId);
    }
    
    private static boolean inited = false;
    private static AsyncCallback<Void> loadCallback;
    private static void staticInit(AsyncCallback<Void> callback)
    {
        if (inited) {
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

    private static final void scriptArrived()
    {
       loadCallback.onSuccess(null);
    }
    
    private static native final void waitForElem(Element elem)
    /*-{
        elem.onload = function() {
            @com.project.canvas.client.shared.nicedit.NicEditor::scriptArrived()();
        };
    }-*/;

    public String getContent() {
        return NicEditor.getContent(nativeNicEditor);
    }
    
    private native static final String getContent(JavaScriptObject nicInstance) /*-{
        return nicInstance.getContent();
    }-*/;

    private static native final JavaScriptObject nativeCreateNicEdit(String id)
    /*-{
        var e = $wnd.nicEditor;
        var inst = new e();
        inst.panelInstance(id);
        return inst;
    }-*/;
    
}
