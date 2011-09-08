package com.project.website.canvas.client.canvastools.textedit;

import com.project.shared.client.net.DynamicSourceLoader;

public class AlohaEditor
{

    private static final String ALOHA_SCRIPT_URL = "aloha/aloha.js";

    public static void loadApi()
    {
        DynamicSourceLoader.getLoadAsyncFunc(ALOHA_SCRIPT_URL).run(null);
    }
}
