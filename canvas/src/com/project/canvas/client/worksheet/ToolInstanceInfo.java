package com.project.canvas.client.worksheet;

import java.util.Date;

import com.google.gwt.event.shared.HandlerRegistration;
import com.project.canvas.client.canvastools.base.CanvasToolFactory;
import com.project.canvas.client.canvastools.base.CanvasToolFrame;
import com.project.shared.client.handlers.RegistrationsManager;

class ToolInstanceInfo {
    public ToolInstanceInfo(CanvasToolFactory<?> factory, CanvasToolFrame toolFrame,
            HandlerRegistration killRegistration) {
        super();
        this.factory = factory;
        this.killRegistration = killRegistration;
        this.createdOn = new Date();
        this.toolFrame = toolFrame;
    }

    CanvasToolFrame toolFrame;
    HandlerRegistration killRegistration;
    Date createdOn;
    CanvasToolFactory<?> factory;
    RegistrationsManager registrations = new RegistrationsManager();
}