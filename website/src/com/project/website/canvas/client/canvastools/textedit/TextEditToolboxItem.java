package com.project.website.canvas.client.canvastools.textedit;

import com.project.website.canvas.client.canvastools.base.interfaces.CanvasTool;
import com.project.website.canvas.client.canvastools.base.interfaces.CanvasToolFactory;
import com.project.website.canvas.client.canvastools.base.interfaces.ToolboxItem;
import com.project.website.canvas.client.resources.CanvasResources;
import com.project.website.canvas.shared.data.ElementData;

public class TextEditToolboxItem implements ToolboxItem {
    private static TextEditToolFactory factory = new TextEditToolFactory();

    public String getToolboxIconStyle() {
        return CanvasResources.INSTANCE.main().toolboxTextIconStyle();
    }

    public String getCanvasStyleInCreateMode() {
        return CanvasResources.INSTANCE.main().textBoxCreateModeCanvasStyle();
    }

    public String getDragIconStyle() {
        return "";
    }

    public String getToolboxIconToolTip() {
        return "Text";
    }

    public CanvasToolFactory<? extends CanvasTool<? extends ElementData>> getToolFactory() {
        // TODO Auto-generated method stub
        return TextEditToolboxItem.factory;
    }

    @Override
    public boolean createOnMouseDown()
    {
        // TODO Auto-generated method stub
        return false;
    }
}
