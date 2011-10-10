package com.project.website.canvas.client.canvastools.base.interfaces;

import com.project.website.canvas.shared.data.ElementData;

public interface ToolboxItem {
    String getToolboxIconStyle();
    String getDragIconStyle();
    String getCanvasStyleInCreateMode();
    String getToolboxIconToolTip();

    CanvasToolFactory<? extends CanvasTool<? extends ElementData>> getToolFactory();

    boolean createOnMouseDown();
}
