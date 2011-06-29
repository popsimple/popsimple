package com.project.website.canvas.client.canvastools;

import com.project.website.canvas.client.canvastools.base.CanvasTool;
import com.project.website.canvas.client.canvastools.base.CanvasToolFactory;
import com.project.website.canvas.client.canvastools.base.ToolboxItem;
import com.project.website.canvas.client.resources.CanvasResources;
import com.project.website.canvas.shared.data.ElementData;

public class CursorToolboxItem implements ToolboxItem {
    public String getToolboxIconStyle() {
        return CanvasResources.INSTANCE.main().toolboxCursorIconStyle();
    }

    public String getCanvasStyleInCreateMode() {
        return CanvasResources.INSTANCE.main().cursorCreateModeCanvasStyle();
    }

    public String getDragIconStyle() {
        return "";
    }

    public String getToolboxIconToolTip() {
        return "Cursor";
    }

    public CanvasToolFactory<CanvasTool<ElementData>> getToolFactory() {
        // TODO Auto-generated method stub
        return null;
    }
}
