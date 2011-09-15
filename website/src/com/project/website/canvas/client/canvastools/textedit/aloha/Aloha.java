package com.project.website.canvas.client.canvastools.textedit.aloha;

import com.google.gwt.dom.client.Element;

public class Aloha
{

    public static Editable registerElementById(Element elem)
    {
        String id = elem.getId();
        Aloha.registerElementByQuery("#" + id);
        return Aloha.getEditableById(id);
    }

    private static native void registerElementByQuery(String query)
    /*-{
        $wnd.jQuery(query).aloha();
    }-*/;


    private static native Editable getEditableById(String id)
    /*-{
        return $wnd.GENTICS.Aloha.getEditableById(id);
    }-*/;


//    public static native void setFloatingMenuVisible(boolean isVisible)
//    /*-{
//        if (isVisible) {
//            $wnd.jQuery('.GENTICS_floatingmenu').show();
//            $wnd.jQuery('#GENTICS_floatingmenu_shadow').show();
//        }
//        else {
//            $wnd.jQuery('.GENTICS_floatingmenu').hide();
//            $wnd.jQuery('#GENTICS_floatingmenu_shadow').hide();
//        }
//    }-*/;

    /**
     * This was supposed to force the floating menu appear/disappear, but it doesn't work.
     */
    public static native void floatingMenuDoLayout()
    /*-{
        //$wnd.GENTICS.Aloha.FloatingMenu.doLayout();
        // HACK
        //$wnd.GENTICS.Aloha.FloatingMenu.setScope("GENTICS.Aloha.continuoustext");
        //$wnd.GENTICS.Aloha.FloatingMenu.doLayout();
        //var rangeObject = $wnd.GENTICS.Aloha.Selection.getRangeObject();
        //if (rangeObject.select) { rangeObject.select(); }
    }-*/;

    // TODO: Doesn't work - aloha can't load dynamically?
//  private static final String ALOHA_CORE_SCRIPT_URL = "aloha/aloha.js";
//  private static final String[] ALOHA_SCRIPT_URLS = new String[] { "aloha/plugins/com.gentics.aloha.plugins.Format/plugin.js",
//  };
//    public static void loadApi()
//    {
//        setBaseAlohaURL();
//
//        AsyncFunc<Void, Void> loaderFunc = AsyncFunc.immediate(); //DynamicSourceLoader.getLoadAsyncFunc(Aloha.ALOHA_CORE_SCRIPT_URL);
//        for (String url : Aloha.ALOHA_SCRIPT_URLS) {
//            loaderFunc = loaderFunc.then(DynamicSourceLoader.getLoadAsyncFunc(url));
//        }
//        loaderFunc.then(new Func.VoidAction() {
//            @Override
//            public void exec()
//            {
//                Aloha.initAlohaPlugins();
//            }
//        }).run(null);
//    }
//
//    private final static native void setBaseAlohaURL()
//    /*-{
//		$wnd.GENTICS_Aloha_base = 'aloha/';
//    }-*/;
//
//    private final static native void initAlohaPlugins()
//    /*-{
//        $wnd.GENTICS.Aloha.Format.init();
//    }-*/;
}
