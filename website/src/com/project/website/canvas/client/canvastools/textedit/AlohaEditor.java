package com.project.website.canvas.client.canvastools.textedit;

import com.google.gwt.dom.client.Element;
import com.project.shared.client.net.DynamicSourceLoader;
import com.project.shared.data.funcs.AsyncFunc;
import com.project.shared.data.funcs.Func;

public class AlohaEditor
{

    private static final String ALOHA_CORE_SCRIPT_URL = "aloha/aloha.js";
    private static final String[] ALOHA_SCRIPT_URLS = new String[] { "aloha/plugins/com.gentics.aloha.plugins.Format/plugin.js",

    };

    public static void registerElementById(Element elem)
    {
        AlohaEditor.registerElementByQuery("#" + elem.getId());
    }

    private static native void registerElementByQuery(String query)
    /*-{
        $wnd.jQuery(query).aloha();
    }-*/;


    // TODO: Doesn't work - aloha can't load dynamically?
    public static void loadApi()
    {
        setBaseAlohaURL();

        AsyncFunc<Void, Void> loaderFunc = AsyncFunc.immediate(); //DynamicSourceLoader.getLoadAsyncFunc(AlohaEditor.ALOHA_CORE_SCRIPT_URL);
        for (String url : AlohaEditor.ALOHA_SCRIPT_URLS) {
            loaderFunc = loaderFunc.then(DynamicSourceLoader.getLoadAsyncFunc(url));
        }
        loaderFunc.then(new Func.VoidAction() {
            @Override
            public void exec()
            {
                AlohaEditor.configAloha();
            }
        }).run(null);
    }

    private final static native void setBaseAlohaURL()
    /*-{
		$wnd.GENTICS_Aloha_base = 'aloha/';
    }-*/;

    private final static native void configAloha()
    /*-{
        $wnd.GENTICS.Aloha.Format.init();
        //$wnd.console.error("Loading...");
//        if (!$wnd.jQuery.isAloha && $wnd.console && $wnd.console.error) {
//            $wnd.console.error("Aloha ERROR: jQuery was included at least a second time after loading Aloha. " +
//                "This will cause serious problems. You must not load other versions " +
//                "of jQuery with Aloha.");
//        }
//
//		if ($wnd.Ext.isReady) {
//			$wnd.GENTICS.Aloha.init();
//		} else {
//			$wnd.Ext.onReady(function() {
//				$wnd.GENTICS.Aloha.init();
//			});
//		}
    }-*/;
}
