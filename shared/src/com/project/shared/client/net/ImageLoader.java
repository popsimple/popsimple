package com.project.shared.client.net;

import com.google.common.base.Strings;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.handlers.RegistrationsManager;
import com.project.shared.data.KeyValue;

public class ImageLoader
{
    private Image _image = new Image();
    private RegistrationsManager _selfRegs = new RegistrationsManager();
    private RegistrationsManager _imageRegs = new RegistrationsManager();
    private SimpleEvent<KeyValue<Integer, Image>> _loadHandler = new SimpleEvent<KeyValue<Integer, Image>>();
    private SimpleEvent<Void> _errorHandler = new SimpleEvent<Void>();

    public ImageLoader()
    {
        //Image is still loaded even if it sets to display:none.
        this._image.getElement().getStyle().setProperty("display", "none");
    }

    public HandlerRegistration addLoadHandler(SimpleEvent.Handler<KeyValue<Integer, Image>> loadHandler)
    {
        return this._loadHandler.addHandler(loadHandler);
    }

    public HandlerRegistration addErrorHandler(SimpleEvent.Handler<Void> errorHandler)
    {
        return this._errorHandler.addHandler(errorHandler);
    }

    public void load(String[] imageUrls)
    {
        this._selfRegs.clear();
        RootPanel.get().remove(this._image);

        RootPanel.get().add(this._image);
        this._selfRegs.add(this._loadHandler.addHandler(new SimpleEvent.Handler<KeyValue<Integer, Image>>(){
            @Override
            public void onFire(KeyValue<Integer, Image> arg) {
                _selfRegs.clear();
                RootPanel.get().remove(_image);
            }}));
        this._selfRegs.add(this._errorHandler.addHandler(new SimpleEvent.Handler<Void>(){
            @Override
            public void onFire(Void arg) {
                _selfRegs.clear();
                RootPanel.get().remove(_image);
            }}));
        this.load(this._image, imageUrls, 0);
    }

    public void load(Image image, String[] imageUrls)
    {
        this.load(image, imageUrls, 0);
    }

    private void load(final Image image, final String[] imageUrls, final int index)
    {
        this._imageRegs.clear();
        String imageUrl = imageUrls[index];
        if (Strings.isNullOrEmpty(imageUrl))
        {
            _errorHandler.dispatch(null);
        }
        this._imageRegs.add(image.addErrorHandler(new ErrorHandler() {
            @Override
            public void onError(ErrorEvent event) {
                _imageRegs.clear();
                if (index >= imageUrls.length - 1)
                {
                    _errorHandler.dispatch(null);
                }
                else
                {
                    load(image, imageUrls, index + 1);
                }
            }}));

        this._imageRegs.add(image.addLoadHandler(new LoadHandler() {
            @Override
            public void onLoad(LoadEvent event) {
                _imageRegs.clear();
                _loadHandler.dispatch(new KeyValue<Integer, Image>(index, image));
            }
        }));
        Image.prefetch(imageUrl);
        image.setUrl(imageUrl); // will be set to "" after loaded.
    }
}
