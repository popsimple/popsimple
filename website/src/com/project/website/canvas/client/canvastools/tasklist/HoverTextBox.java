package com.project.website.canvas.client.canvastools.tasklist;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.TextBox;
import com.project.shared.client.handlers.RegistrationsManager;
import com.project.website.canvas.client.resources.CanvasResources;

public class HoverTextBox extends TextBox {
    private boolean isEditing = false;
    private RegistrationsManager _registrations = new RegistrationsManager();

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

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);

        if (readOnly)
        {
            this._registrations.clear();
            this.enterViewMode();
        }
        else
        {
            this.registerHandlers();
        }
    }

    private void registerHandlers() {
        this._registrations.add(this.addBlurHandler(new BlurHandler() {
            public void onBlur(BlurEvent event) {
                stopEditing();
            }
        }));
        this._registrations.add(this.addMouseOverHandler(new MouseOverHandler() {
            public void onMouseOver(MouseOverEvent event) {
                enterEditMode();
            }
        }));
        this._registrations.add(this.addFocusHandler(new FocusHandler() {

            public void onFocus(FocusEvent event) {
                startEditing();
            }
        }));
        this._registrations.add(this.addMouseOutHandler(new MouseOutHandler() {
            public void onMouseOut(MouseOutEvent event) {
                enterViewMode();
            }
        }));
    }
}
