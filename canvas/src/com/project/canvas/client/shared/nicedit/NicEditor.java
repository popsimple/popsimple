package com.project.canvas.client.shared.nicedit;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ScriptElement;

public class NicEditor
{
    private static int id = 0;
    private final JavaScriptObject nativeNicEditor;

    public NicEditor(Element element) {
        NicEditor.staticInit();
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
    private static void staticInit()
    {
        if (inited) {
            return;
        }
        inited = true;
        ScriptElement elem = Document.get().createScriptElement();
        elem.setSrc("nicEdit/nicEdit.js");
        elem.setLang("javascript");
        elem.setType("text/javascript");
        Document.get().getElementsByTagName("head").getItem(0).appendChild(elem);
    }

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
