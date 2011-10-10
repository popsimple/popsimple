package com.project.website.canvas.client.canvastools.tasklist;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.TextBox;
import com.project.website.canvas.client.resources.CanvasResources;

public class HoverTextBox extends TextBox {
    private boolean isEditing = false;

    public HoverTextBox() {
        this.enterViewMode();
        this.registerHandlers();
    }

    private void enterViewMode() {
        this.removeStyleName(CanvasResources.INSTANCE.main().hoverTextBoxEdit());
        this.addStyleName(CanvasResources.INSTANCE.main().hoverTextBoxView());
    }

    private void enterEditMode() {
        this.removeStyleName(CanvasResources.INSTANCE.main().hoverTextBoxView());
        this.addStyleName(CanvasResources.INSTANCE.main().hoverTextBoxEdit());
    }

    private void startEditing() {
        if (this.isEditing)
        {
            return;
        }
        enterEditMode();
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {

            @Override
            public void execute() {
                selectAll();
            }
        });
        isEditing = true;
    }

    private void stopEditing() {
        if (false == this.isEditing)
        {
            return;
        }
        enterViewMode();
        this.setCursorPos(0);
        isEditing = false;
    }

    private void registerHandlers() {
        this.addBlurHandler(new BlurHandler() {
            public void onBlur(BlurEvent event) {
                stopEditing();
            }
        });
        this.addMouseOverHandler(new MouseOverHandler() {
            public void onMouseOver(MouseOverEvent event) {
                enterEditMode();
            }
        });
        this.addFocusHandler(new FocusHandler() {

            public void onFocus(FocusEvent event) {
                startEditing();
            }
        });
        this.addMouseOutHandler(new MouseOutHandler() {
            public void onMouseOut(MouseOutEvent event) {
                enterViewMode();
            }
        });
    }
}
