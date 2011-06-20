package com.project.canvas.client.shared.dialogs;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.project.canvas.client.shared.NativeUtils;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.client.shared.searchProviders.interfaces.MediaInfo;
import com.project.canvas.client.shared.searchProviders.interfaces.MediaSearchProvider;
import com.project.canvas.client.shared.widgets.media.MediaSearchPanel;
import com.project.canvas.shared.UrlUtils;
import com.project.canvas.shared.data.ImageInformation;
import com.project.canvas.shared.data.Point2D;

public class SelectImageDialog extends Composite implements TakesValue<ImageInformation>, Focusable {

    private static MediaToolOptionsUiBinder uiBinder = GWT.create(MediaToolOptionsUiBinder.class);

    interface MediaToolOptionsUiBinder extends UiBinder<Widget, SelectImageDialog> {
    }

    @UiField
    FlowPanel formPanel;

    @UiField
    TextBox urlTextBox;

    @UiField
    Button doneButton;

    @UiField
    Button cancelButton;

    @UiField
    MediaSearchPanel mediaSearchPanel;

    @UiField
    CheckBox stretchXOption;

    @UiField
    CheckBox stretchYOption;

    @UiField
    CheckBox repeatOption;

    @UiField
    CheckBox centerOption;

    private SimpleEvent<ImageInformation> doneEvent = new SimpleEvent<ImageInformation>();
    private SimpleEvent<Void> cancelEvent = new SimpleEvent<Void>();

    private ImageInformation _imageInformation = new ImageInformation();

    public SelectImageDialog() {
        initWidget(uiBinder.createAndBindUi(this));
        this.doneButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                doneClicked();
            }
        });
        this.cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                cancelEvent.dispatch(null);
            }
        });
        this.mediaSearchPanel.addMediaPickedHandler(new SimpleEvent.Handler<MediaInfo>() {
            @Override
            public void onFire(MediaInfo imageInfo) {
                setSearchData(imageInfo);
            }
        });
        this.urlTextBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                setManualUrl(urlTextBox.getText());
            }
        });
        this.urlTextBox.addKeyPressHandler(new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent event)
            {
                if (NativeUtils.keyIsEnter(event)) {
                    doneClicked();
                }
            }
        });
    }

    public HandlerRegistration addDoneHandler(SimpleEvent.Handler<ImageInformation> handler)
    {
        return this.doneEvent.addHandler(handler);
    }

    public HandlerRegistration addCancelHandler(SimpleEvent.Handler<Void> handler)
    {
        return this.cancelEvent.addHandler(handler);
    }

    public void setSearchProviders(List<? extends MediaSearchProvider> searchProviders)
    {
        this.mediaSearchPanel.setSearchProviders(searchProviders);
    }

    @Override
    public void setValue(ImageInformation value) {
        this._imageInformation = value;
        this.urlTextBox.setText(value.url);
    }

    @Override
    public ImageInformation getValue() {
        return this._imageInformation;
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

    private void setSearchData(MediaInfo mediaInfo)
    {
        this._imageInformation.url = mediaInfo.getMediaUrl();
        this._imageInformation.size = new Point2D(mediaInfo.getWidth(), mediaInfo.getHeight());
        this.urlTextBox.setText(this._imageInformation.url);
    }

    private void applyImageOptions()
    {
        this._imageInformation.repeat = this.repeatOption.getValue();
        this._imageInformation.center = this.centerOption.getValue();
        this._imageInformation.stretchWidth = this.stretchXOption.getValue();
        this._imageInformation.stretchHeight = this.stretchYOption.getValue();
    }

    private void setManualUrl(String url)
    {
        if (url.isEmpty() || UrlUtils.isValidUrl(url, false)) {
            this._imageInformation.url = url;
            this._imageInformation.size = new Point2D();
        } else {
            Window.alert("Invalid url.");
        }
    }

    public void doneClicked()
    {
        this.applyImageOptions();
        this.doneEvent.dispatch(this._imageInformation);
    }
}
