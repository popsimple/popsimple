package com.project.website.canvas.client.worksheet.interfaces;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.website.canvas.shared.data.CanvasPageOptions;

public interface WorksheetOptionsView extends TakesValue<CanvasPageOptions>, IsWidget
{
    HandlerRegistration addCancelHandler(Handler<Void> handler);

    HandlerRegistration addDoneHandler(Handler<Void> handler);

}