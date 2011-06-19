package com.project.canvas.client.shared.widgets.media.images;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.project.canvas.client.shared.events.SimpleEvent.Handler;
import com.project.canvas.client.shared.searchProviders.interfaces.MediaInfo;
import com.project.canvas.client.shared.searchProviders.interfaces.MediaSearchProvider;
import com.project.canvas.client.shared.widgets.media.IMediaSearchPanel;
import com.project.canvas.client.shared.widgets.media.MediaSearchPanel;
import com.project.canvas.shared.data.ImageInformation;
import com.project.canvas.shared.data.Point2D;

public class ImageSearchPanel extends Composite implements IMediaSearchPanel<ImageInformation> {

    private static ImageSearchPanelUiBinder uiBinder = GWT.create(ImageSearchPanelUiBinder.class);

    interface ImageSearchPanelUiBinder extends UiBinder<Widget, ImageSearchPanel> {
    }

    @UiField
    public MediaSearchPanel mediaSearchPanel;

    @UiField
    CheckBox stretchXOption;

    @UiField
    CheckBox stretchYOption;

    @UiField
    CheckBox repeatOption;

    @UiField
    CheckBox centerOption;


    public ImageSearchPanel() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public HandlerRegistration addMediaPickedHandler(Handler<MediaInfo> handler) {
        return this.mediaSearchPanel.addMediaPickedHandler(handler);
    }

    @Override
    public void setSearchProviders(List<? extends MediaSearchProvider> searchProviders) {
        this.mediaSearchPanel.setSearchProviders(searchProviders);
    }

    @Override
    public void setValue(ImageInformation value) {
        // TODO Auto-generated method stub

    }

    @Override
    public ImageInformation getValue() {
        ImageInformation imageInfo = new ImageInformation();
        MediaInfo mediaInfo = this.mediaSearchPanel.getSelectedMedia();
        if (null != mediaInfo)
        {
            imageInfo.url = mediaInfo.getMediaUrl();
            imageInfo.size = new Point2D(mediaInfo.getWidth(), mediaInfo.getHeight());
        }
        imageInfo.repeat = this.repeatOption.getValue();
        imageInfo.center = this.centerOption.getValue();
        imageInfo.stretchWidth = this.stretchXOption.getValue();
        imageInfo.stretchHeight = this.stretchYOption.getValue();
        return imageInfo;
    }

}
