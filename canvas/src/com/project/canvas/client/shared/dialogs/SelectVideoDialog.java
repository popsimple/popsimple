package com.project.canvas.client.shared.dialogs;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.client.shared.handlers.SpecificKeyPressHandler;
import com.project.canvas.client.shared.searchProviders.interfaces.MediaInfo;
import com.project.canvas.client.shared.searchProviders.interfaces.MediaSearchProvider;
import com.project.canvas.client.shared.widgets.media.MediaSearchPanel;
import com.project.canvas.shared.ObjectUtils;
import com.project.canvas.shared.UrlUtils;
import com.project.canvas.shared.data.Point2D;
import com.project.canvas.shared.data.VideoInformation;

public class SelectVideoDialog extends Composite implements TakesValue<VideoInformation>, Focusable {

    private static MediaToolOptionsUiBinder uiBinder = GWT.create(MediaToolOptionsUiBinder.class);

    interface MediaToolOptionsUiBinder extends UiBinder<Widget, SelectVideoDialog> {
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

    private SimpleEvent<VideoInformation> doneEvent = new SimpleEvent<VideoInformation>();
    private SimpleEvent<Void> cancelEvent = new SimpleEvent<Void>();

    private VideoInformation _videoInformation = new VideoInformation();

    public SelectVideoDialog() {
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
            public void onFire(MediaInfo mediaInfo) {
                setSearchData(mediaInfo);
            }
        });
        this.urlTextBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                setManualUrl(urlTextBox.getText());
            }
        });
        this.urlTextBox.addKeyPressHandler(new SpecificKeyPressHandler(KeyCodes.KEY_ENTER) {
            @Override
            public void onSpecificKeyPress(KeyPressEvent event) {
                setManualUrl(urlTextBox.getText());
                doneClicked();
            }
        });
    }

    public HandlerRegistration addDoneHandler(SimpleEvent.Handler<VideoInformation> handler)
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
    public void setValue(VideoInformation value) {
        this._videoInformation = value;
        this.urlTextBox.setText(value.url);
    }

    @Override
    public VideoInformation getValue() {
        return this._videoInformation;
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
        this._videoInformation.url = mediaInfo.getMediaUrl();
        this._videoInformation.size = new Point2D(mediaInfo.getWidth(), mediaInfo.getHeight());
        this.urlTextBox.setText(this._videoInformation.url);
    }

    private void setManualUrl(String url)
    {
        if (ObjectUtils.equals(this._videoInformation.url, url))
        {
            return;
        }
        if (url.isEmpty() || UrlUtils.isValidUrl(url, false)) {
            this._videoInformation.url = url;
            this._videoInformation.size = new Point2D();
        } else {
            Window.alert("Invalid url.");
        }
    }

    public void doneClicked()
    {
        this.doneEvent.dispatch(this._videoInformation);
    }
}