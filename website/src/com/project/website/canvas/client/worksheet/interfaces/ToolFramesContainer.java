package com.project.website.canvas.client.worksheet.interfaces;

import java.util.Map.Entry;
import java.util.Set;

import com.project.website.canvas.client.canvastools.base.interfaces.CanvasToolFrame;
import com.project.website.canvas.client.worksheet.data.CanvasToolFrameInfo;

public interface ToolFramesContainer {
    Iterable<CanvasToolFrame> getToolFrames();
    CanvasToolFrameInfo addToolFrame(CanvasToolFrame toolFrame);
    void removeToolFrame(CanvasToolFrame toolFrame);
    
    Set<CanvasToolFrame> getHoveredToolFrames();
    
    void setIsEditMode(boolean isEditMode);
    Set<Entry<CanvasToolFrame, CanvasToolFrameInfo>> getToolFrameInfos();
}
