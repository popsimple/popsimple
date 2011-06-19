package com.project.canvas.client.shared.dialogs;

import java.util.List;

import com.google.gwt.core.client.GWT;
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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.project.canvas.client.shared.NativeUtils;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.client.shared.searchProviders.interfaces.MediaInfo;
import com.project.canvas.client.shared.searchProviders.interfaces.MediaSearchProvider;
import com.project.canvas.client.shared.widgets.media.IMediaSearchPanel;
import com.project.canvas.client.shared.widgets.media.images.ImageSearchPanel;
import com.project.canvas.shared.UrlUtils;
import com.project.canvas.shared.data.ImageInformation;

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
    HTMLPanel searchPanelContainer;

    private IMediaSearchPanel<ImageInformation> _searchPanel = new ImageSearchPanel();

    private SimpleEvent<ImageInformation> doneEvent = new SimpleEvent<ImageInformation>();
    private SimpleEvent<Void> cancelEvent = new SimpleEvent<Void>();

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
        this.searchPanelContainer.add(this._searchPanel);
        this._searchPanel.addMediaPickedHandler(new SimpleEvent.Handler<MediaInfo>() {
            @Override
            public void onFire(MediaInfo imageInfo) {
                urlTextBox.setText(imageInfo.getMediaUrl());
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

    public HandlerRegistration addDoneEvent(SimpleEvent.Handler<ImageInformation> handler)
    {
        return this.doneEvent.addHandler(handler);
    }

    public HandlerRegistration addCancelEvent(SimpleEvent.Handler<Void> handler)
    {
        return this.cancelEvent.addHandler(handler);
    }

    public void setSearchProviders(List<? extends MediaSearchProvider> searchProviders)
    {
        this._searchPanel.setSearchProviders(searchProviders);
    }

    @Override
    public void setValue(ImageInformation value) {
        this.urlTextBox.setText(value.url);
        this._searchPanel.setValue(value);
    }

    @Override
    public ImageInformation getValue() {
        return this._searchPanel.getValue();
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

    public void doneClicked()
    {
        boolean empty = urlTextBox.getText().trim().isEmpty();
        boolean valid = UrlUtils.isValidUrl(urlTextBox.getText(), false);
        if (empty || valid) {
            doneEvent.dispatch(this._searchPanel.getValue());
        } else {
            Window.alert("Invalid url.");
        }
    }
}
