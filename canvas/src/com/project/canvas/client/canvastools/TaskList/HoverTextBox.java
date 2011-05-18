package com.project.canvas.client.canvastools.TaskList;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.TextBox;
import com.project.canvas.client.resources.CanvasResources;

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
        enterEditMode();
        isEditing = true;
    }

    private void stopEditing() {
        enterViewMode();
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
                // TODO Auto-generated method stub
                startEditing();
            }
        });
        this.addMouseOutHandler(new MouseOutHandler() {
            public void onMouseOut(MouseOutEvent event) {
                if (false == isEditing) {
                    enterViewMode();
                }
            }
        });
    }
}
