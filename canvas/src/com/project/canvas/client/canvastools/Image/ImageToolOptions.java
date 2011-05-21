package com.project.canvas.client.canvastools.Image;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.project.canvas.client.shared.dialogs.ImagePicker;
import com.project.canvas.client.shared.dialogs.ImagePicker.ImageInfo;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.shared.UrlUtils;
import com.project.canvas.shared.data.ImageData;

public class ImageToolOptions extends Composite implements TakesValue<ImageData>, Focusable {

    private static ImageToolOptionsUiBinder uiBinder = GWT.create(ImageToolOptionsUiBinder.class);

    interface ImageToolOptionsUiBinder extends UiBinder<Widget, ImageToolOptions> {
    }

    @UiField
    FormPanel formPanel;

    @UiField
    TextBox urlTextBox;

    @UiField
    Button doneButton;

    @UiField
    Button cancelButton;

    @UiField
    ImagePicker imagePicker;

    private ImageData data;

    private SimpleEvent<Void> doneEvent = new SimpleEvent<Void>();

    private SimpleEvent<Void> cancelEvent = new SimpleEvent<Void>();

    public ImageToolOptions() {
        initWidget(uiBinder.createAndBindUi(this));
        this.doneButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                boolean empty = urlTextBox.getText().trim().isEmpty();
                boolean valid = UrlUtils.isValidUrl(urlTextBox.getText(), false);
                if (empty || valid) {
                    doneEvent.dispatch(null);
                } else {
                    Window.alert("Invalid url.");
                }
            }
        });
        this.cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                cancelEvent.dispatch(null);
            }
        });
        this.imagePicker.addImagePickedHandler(new SimpleEvent.Handler<ImageInfo>() {
            @Override
            public void onFire(ImageInfo imageInfo) {
                urlTextBox.setText(imageInfo.url);
            }
        });
        this.formPanel.addSubmitHandler(new SubmitHandler() {
            @Override
            public void onSubmit(SubmitEvent event) {
                event.cancel();
            }
        });
    }

    SimpleEvent<Void> getDoneEvent() {
        return this.doneEvent;
    }

    SimpleEvent<Void> getCancelEvent() {
        return this.cancelEvent;
    }

    @Override
    public void setValue(ImageData value) {
        this.data = value;
        this.urlTextBox.setText(value.url);
    }

    @Override
    public ImageData getValue() {
        this.data.url = this.urlTextBox.getText();
        return this.data;
    }

    @Override
    public int getTabIndex() {
        return this.urlTextBox.getTabIndex();
    }

    @Override
    public void setAccessKey(char key) {
        this.urlTextBox.setAccessKey(key);
    }

    @Override
    public void setFocus(boolean focused) {
        this.urlTextBox.setFocus(focused);
    }

    @Override
    public void setTabIndex(int index) {
        this.urlTextBox.setTabIndex(index);
    }
}
