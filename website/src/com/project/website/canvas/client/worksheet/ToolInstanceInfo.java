package com.project.website.canvas.client.worksheet;

import java.util.Date;

import com.google.gwt.event.shared.HandlerRegistration;
import com.project.shared.client.handlers.RegistrationsManager;
import com.project.website.canvas.client.canvastools.base.CanvasToolFrameImpl;
import com.project.website.canvas.client.canvastools.base.interfaces.CanvasToolFactory;

class ToolInstanceInfo {
    public ToolInstanceInfo(CanvasToolFactory<?> factory, CanvasToolFrameImpl toolFrame,
            HandlerRegistration killRegistration) {
        super();
        this.factory = factory;
        this.killRegistration = killRegistration;
        this.createdOn = new Date();
        this.toolFrame = toolFrame;
    }

    CanvasToolFrameImpl toolFrame;
    HandlerRegistration killRegistration;
    Date createdOn;
    CanvasToolFactory<?> factory;
    RegistrationsManager registrations = new RegistrationsManager();
}