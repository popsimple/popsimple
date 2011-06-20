package com.project.canvas.client.canvastools.media;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
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
import com.project.canvas.client.shared.searchProviders.interfaces.MediaSearchProvider;
import com.project.canvas.shared.UrlUtils;
import com.project.canvas.shared.data.MediaData;

public class MediaToolOptions extends Composite implements TakesValue<MediaData>, Focusable {

    private static MediaToolOptionsUiBinder uiBinder = GWT.create(MediaToolOptionsUiBinder.class);

    interface MediaToolOptionsUiBinder extends UiBinder<Widget, MediaToolOptions> {
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

    private MediaData data;
//    private IMediaSearchPanel _searchPanel = new ImageSearchPanel();

    private SimpleEvent<Void> doneEvent = new SimpleEvent<Void>();
    private SimpleEvent<Void> cancelEvent = new SimpleEvent<Void>();

    public MediaToolOptions() {
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
//        this.searchPanelContainer.add(this._searchPanel);
//        this._searchPanel.addMediaPickedHandler(new SimpleEvent.Handler<MediaInfo>() {
//            @Override
//            public void onFire(MediaInfo imageInfo) {
//                urlTextBox.setText(imageInfo.getMediaUrl());
//            }
//        });
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

    public SimpleEvent<Void> getDoneEvent() {
        return this.doneEvent;
    }

    public SimpleEvent<Void> getCancelEvent() {
        return this.cancelEvent;
    }

    public void setSearchProviders(List<? extends MediaSearchProvider> searchProviders)
    {
//        this._searchPanel.setSearchProviders(searchProviders);
    }

    @Override
    public void setValue(MediaData value) {
        this.data = value;
        this.urlTextBox.setText(value.url);
    }

    @Override
    public MediaData getValue() {
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

    public void doneClicked()
    {
        boolean empty = urlTextBox.getText().trim().isEmpty();
        boolean valid = UrlUtils.isValidUrl(urlTextBox.getText(), false);
        if (empty || valid) {
            doneEvent.dispatch(null);
        } else {
            Window.alert("Invalid url.");
        }
    }
}
