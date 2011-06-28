package com.project.website.canvas.client.shared.dialogs;

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
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.handlers.SpecificKeyPressHandler;
import com.project.shared.data.Point2D;
import com.project.shared.utils.CloneableUtils;
import com.project.shared.utils.ObjectUtils;
import com.project.shared.utils.UrlUtils;
import com.project.website.canvas.client.shared.ImageOptionTypes;
import com.project.website.canvas.client.shared.ImageOptionsProvider;
import com.project.website.canvas.client.shared.ImageOptionsProviderUtils;
import com.project.website.canvas.client.shared.searchProviders.interfaces.MediaInfo;
import com.project.website.canvas.client.shared.searchProviders.interfaces.MediaSearchProvider;
import com.project.website.canvas.client.shared.widgets.media.MediaSearchPanel;
import com.project.website.canvas.shared.data.ImageInformation;

public class SelectImageDialog extends Composite implements TakesValue<ImageInformation>, Focusable {

    private static MediaToolOptionsUiBinder uiBinder = GWT.create(MediaToolOptionsUiBinder.class);

    interface MediaToolOptionsUiBinder extends UiBinder<Widget, SelectImageDialog> {
    }

    @UiField
    FlowPanel formPanel;

    @UiField
    TextBox urlTextBox;

    @UiField
    Button clearButton;

    @UiField
    Button doneButton;

    @UiField
    Button cancelButton;

    @UiField
    MediaSearchPanel mediaSearchPanel;

    @UiField
    RadioButton stretchOption;

    @UiField
    RadioButton repeatOption;

    @UiField
    RadioButton originalSizeOption;

    private SimpleEvent<ImageInformation> doneEvent = new SimpleEvent<ImageInformation>();
    private SimpleEvent<Void> cancelEvent = new SimpleEvent<Void>();
    private ImageOptionsProvider _imageOptionsProvider;

    private ImageInformation _defaultInformation = new ImageInformation();
    private ImageInformation _imageInformation = new ImageInformation();

    public SelectImageDialog() {
        initWidget(uiBinder.createAndBindUi(this));
        this.clearButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                clear();
            }
        });
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
        this.urlTextBox.addKeyPressHandler(new SpecificKeyPressHandler(KeyCodes.KEY_ENTER) {
            @Override
            public void onSpecificKeyPress(KeyPressEvent event) {
                setManualUrl(urlTextBox.getText());
                doneClicked();
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

    public void setImageOptionsProvider(ImageOptionsProvider imageOptionsProvider)
    {
        this._imageOptionsProvider = imageOptionsProvider;
        this._defaultInformation.options = imageOptionsProvider.getDefaultOptions();
    }

    @Override
    public void setValue(ImageInformation value) {
        this._imageInformation = value;
        if (this.isAttached())
        {
            this.bindData();
        }
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        this.bindData();
    }

    private void bindData()
    {
        this.urlTextBox.setText(this._imageInformation.url);
        switch (ImageOptionsProviderUtils.getImageOptionType(
                this._imageOptionsProvider, this._imageInformation.options))
        {
            case OriginalSize:
                this.originalSizeOption.setValue(true, true);
                break;
            case Repeat:
                this.repeatOption.setValue(true, true);
                break;
            case Stretch:
                this.stretchOption.setValue(true, true);
                break;
            default:
                break;
        }
    }

    private void applyBasicImageOptions()
    {
        if (this.originalSizeOption.getValue())
        {
            ImageOptionsProviderUtils.setImageOptions(this._imageOptionsProvider,
                    this._imageInformation.options, ImageOptionTypes.OriginalSize);
        }
        else if (this.stretchOption.getValue())
        {
            ImageOptionsProviderUtils.setImageOptions(this._imageOptionsProvider,
                    this._imageInformation.options, ImageOptionTypes.Stretch);
        }
        else if (this.repeatOption.getValue())
        {
            ImageOptionsProviderUtils.setImageOptions(this._imageOptionsProvider,
                    this._imageInformation.options, ImageOptionTypes.Repeat);
        }
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

    private void setManualUrl(String url)
    {
        if (ObjectUtils.equals(this._imageInformation.url, url))
        {
            return;
        }
        if (url.isEmpty() || UrlUtils.isValidUrl(url, false)) {
            this._imageInformation.url = url;
            this._imageInformation.size = new Point2D();
        } else {
            Window.alert("Invalid url.");
        }
    }

    public void doneClicked()
    {
        this.applyBasicImageOptions();
        this.doneEvent.dispatch(this._imageInformation);
    }

    public void clear()
    {
        this.mediaSearchPanel.clear();
        this.setValue((ImageInformation)CloneableUtils.clone(this._defaultInformation));
    }
}
