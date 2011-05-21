package com.project.canvas.client.worksheet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.shared.UrlUtils;
import com.project.canvas.shared.GenericUtils;
import com.project.canvas.shared.data.CanvasPageOptions;

public class WorksheetOptionsWidget extends Composite implements TakesValue<CanvasPageOptions> {

    private static WorksheetOptionsWidgetUiBinder uiBinder = GWT.create(WorksheetOptionsWidgetUiBinder.class);

    interface WorksheetOptionsWidgetUiBinder extends UiBinder<Widget, WorksheetOptionsWidget> {
    }

    @UiField
    TextBox urlTextBox;
    @UiField
    Button doneButton;
    @UiField
    Button cancelButton;
    @UiField
    Label urlErrorLabel;
    @UiField
    Label imagePreview;
    @UiField
    CheckBox stretchXOption;
    @UiField
    CheckBox stretchYOption;
    @UiField
    CheckBox repeatOption;
    @UiField
    CheckBox centerOption;

    private CanvasPageOptions value = new CanvasPageOptions();

    protected final SimpleEvent<Void> doneEvent = new SimpleEvent<Void>();
    protected final SimpleEvent<Void> cancelEvent = new SimpleEvent<Void>();

    public SimpleEvent<Void> getDoneEvent() {
        return doneEvent;
    }

    public SimpleEvent<Void> getCancelEvent() {
        return cancelEvent;
    }

    public WorksheetOptionsWidget() {
        initWidget(uiBinder.createAndBindUi(this));
        this.doneButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (validateBackgroundURL()) {
                    doneEvent.dispatch(null);
                } else {
                    Window.alert("Please enter a valid background image url (or leave blank)");
                    urlTextBox.setFocus(true);
                }
            }
        });
        this.cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                cancelEvent.dispatch(null);
            }
        });
        this.urlTextBox.addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
                if (validateBackgroundURL()) {
                    previewImage();
                }
            }
        });
    }

    protected void previewImage() {
        imagePreview.getElement().getStyle()
                .setBackgroundImage("url(" + this.urlTextBox.getText().trim() + ")");
    }

    protected boolean validateBackgroundURL() {
        String url = urlTextBox.getText().trim();
        if (url.isEmpty() || UrlUtils.isValidUrl(url, false)) {
            urlErrorLabel.setText(null);
            return true;
        }
        urlErrorLabel.setText("Invalid background image url");
        return false;
    }

    @Override
    public void setValue(CanvasPageOptions newValue) {
        this.value = newValue != null ? newValue : new CanvasPageOptions();
        this.urlTextBox.setText(GenericUtils.DefaultIfNull(this.value.backgroundImageURL, ""));
        this.repeatOption.setValue(this.value.backgroundRepeat.toLowerCase().trim().equals("repeat"));
        this.stretchXOption.setValue(false);
        this.stretchYOption.setValue(false);
        String[] sizeParts = this.value.backgroundSize.toLowerCase().trim().split(" ");
        if (sizeParts.length > 0) {
            this.stretchXOption.setValue(sizeParts[0].equals("100%"));
        }
        if (sizeParts.length > 1) {
            this.stretchYOption.setValue(sizeParts[1].equals("100%"));
        }
        this.centerOption
                .setValue(this.value.backgroundPosition.toLowerCase().trim().equals("center center"));
        this.previewImage();
    }

    @Override
    public CanvasPageOptions getValue() {
        this.value.backgroundImageURL = this.urlTextBox.getText().trim();
        if (this.repeatOption.getValue()) {
            this.value.backgroundRepeat = "repeat";
        } else {
            this.value.backgroundRepeat = "no-repeat";
        }

        if (this.stretchXOption.getValue()) {
            this.value.backgroundSize = "100% ";
        } else {
            this.value.backgroundSize = "auto ";
        }
        if (this.stretchYOption.getValue()) {
            this.value.backgroundSize += "100%";
        }

        if (this.centerOption.getValue()) {
            this.value.backgroundPosition = "center center";
        } else {
            this.value.backgroundPosition = "";
        }

        return this.value;
    }

}
