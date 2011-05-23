package com.project.canvas.client.shared.nicedit;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ScriptElement;

public class NicEditor
{
    private final JavaScriptObject nativeNicEditor;
    private final JavaScriptObject nativeNicInstance;

    public NicEditor(Element element) {
        NicEditor.staticInit();
        
        nativeNicEditor = NicEditor.nativeCreateNicEdit();
        nativeNicInstance = NicEditor.addInstance(nativeNicEditor, element.getId());
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
        return NicEditor.getContent(nativeNicInstance);
    }
    
    private native static final String getContent(JavaScriptObject nicInstance) /*-{
        return nicInstance.getContent();
    }-*/;

    private static native final JavaScriptObject nativeCreateNicEdit()
    /*-{
        var e = $wnd.nicEditor;
        return new e();
    }-*/;
    
    private static native final JavaScriptObject addInstance(JavaScriptObject nicEditorInstance, String elementId)
    /*-{
        return nicEditorInstance.addInstance(elementId, null);
    }-*/;
}
