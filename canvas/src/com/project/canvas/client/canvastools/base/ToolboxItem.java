package com.project.canvas.client.canvastools.base;

import com.project.canvas.shared.data.ElementData;

public interface ToolboxItem {
    public String getToolboxIconStyle();

    public String getDragIconStyle();

    public String getCanvasStyleInCreateMode();

    public String getToolboxIconToolTip();

    public CanvasToolFactory<? extends CanvasTool<? extends ElementData>> getToolFactory();
}
