package com.project.canvas.client.shared;

import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.project.canvas.client.shared.events.SimpleEvent;

public class ImageLoader
{
    private Image _image = new Image();

    private SimpleEvent<Image> _loadHandler = new SimpleEvent<Image>();
    private SimpleEvent<Void> _errorHandler = new SimpleEvent<Void>();

    public ImageLoader()
    {
        //Image is still loaded even if it sets to display:none.
        this._image.getElement().getStyle().setProperty("display", "none");
    }

    public HandlerRegistration addLoadHandler(SimpleEvent.Handler<Image> loadHandler)
    {
        return this._loadHandler.addHandler(loadHandler);
    }

    public HandlerRegistration addErrorHandler(SimpleEvent.Handler<Void> errorHandler)
    {
        return this._errorHandler.addHandler(errorHandler);
    }

    public void Load(String imageUrl)
    {
        RootPanel.get().add(this._image);
        final RegistrationsManager errorRegs = new RegistrationsManager();
        errorRegs.add(this._image.addErrorHandler(new ErrorHandler() {
            @Override
            public void onError(ErrorEvent event) {
                errorRegs.clear();
                RootPanel.get().remove(_image);
                _errorHandler.dispatch(null);
            }}));

        final RegistrationsManager loadRegs = new RegistrationsManager();
        loadRegs.add(this._image.addLoadHandler(new LoadHandler() {
            @Override
            public void onLoad(LoadEvent event) {
                _loadHandler.dispatch(_image);
                _image.setUrl(""); // don't display the image in the <img>,
                                  // only as background
                loadRegs.clear();
                RootPanel.get().remove(_image);
            }
        }));
        Image.prefetch(imageUrl);
        this._image.setUrl(imageUrl); // will be set to "" after loaded.
    }
}
