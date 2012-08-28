package com.project.website.canvas.client.worksheet.data;

import com.project.shared.client.handlers.KeyedRegistrationsManager;
import com.project.website.canvas.client.canvastools.base.interfaces.CanvasToolFrame;

public class CanvasToolFrameInfo 
{
    private final CanvasToolFrame toolFrame;
    private final KeyedRegistrationsManager _keyedRegs = new KeyedRegistrationsManager();
    
    public CanvasToolFrameInfo(CanvasToolFrame toolFrame) 
    {
        this.toolFrame = toolFrame;
    }
    
    public CanvasToolFrame getToolFrame() {
        return toolFrame;
    }
    
    public KeyedRegistrationsManager getRegistrations() {
        return _keyedRegs;
    }
}
