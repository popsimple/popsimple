package com.project.canvas.client.worksheet;

import java.util.Date;

import com.google.gwt.event.shared.HandlerRegistration;
import com.project.canvas.client.canvastools.base.CanvasToolFactory;
import com.project.canvas.client.canvastools.base.CanvasToolFrame;
import com.project.canvas.client.shared.RegistrationsManager;

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
    @SuppressWarnings("unused")
    Date createdOn;
    @SuppressWarnings("unused")
    CanvasToolFactory<?> factory;
    RegistrationsManager registrations = new RegistrationsManager();
}