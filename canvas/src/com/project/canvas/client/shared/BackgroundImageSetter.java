package com.project.canvas.client.shared;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Image;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.shared.StringUtils;
import com.project.canvas.shared.data.Point2D;

public class BackgroundImageSetter
{
    private SimpleEvent<Void> _loadEvent = new SimpleEvent<Void>();
    private SimpleEvent<Void> _errorEvent = new SimpleEvent<Void>();
    private Element _element = null;
    private RegistrationsManager _regManager = new RegistrationsManager();

    public BackgroundImageSetter(Element element)
    {
        this._element = element;
        this._loadEvent.addHandler(new SimpleEvent.Handler<Void>(){
            @Override
            public void onFire(Void arg) {
                _regManager.clear();
            }});
        this._errorEvent.addHandler(new SimpleEvent.Handler<Void>(){
            @Override
            public void onFire(Void arg) {
                _regManager.clear();
            }});
    }

    public HandlerRegistration addLoadHandler(SimpleEvent.Handler<Void> handler)
    {
        return this._loadEvent.addHandler(handler);
    }

    public HandlerRegistration addErrorHandler(SimpleEvent.Handler<Void> handler)
    {
        return this._errorEvent.addHandler(handler);
    }

    public void SetBackroundImage(final String imageUrl, final boolean autoSize)
    {
        this.SetBackroundImage(imageUrl, "", autoSize);
    }

    public void SetBackroundImage(final String imageUrl, final String errorImageUrl)
    {
        this.SetBackroundImage(imageUrl, errorImageUrl, false);
    }

    public void SetBackroundImage(final String imageUrl,
            final String errorImageUrl, final boolean autoSize)
    {
        this._regManager.clear();
        ImageLoader imageLoader = new ImageLoader();

        this._regManager.add(imageLoader.addErrorHandler(new SimpleEvent.Handler<Void>() {
            @Override
            public void onFire(Void arg) {
                OnImageLoadError(errorImageUrl, autoSize);
            }}));
        this._regManager.add(imageLoader.addLoadHandler(new SimpleEvent.Handler<Image>(){
            @Override
            public void onFire(Image arg) {
                ElementUtils.SetBackgroundImage(_element, arg, autoSize);
                _loadEvent.dispatch(null);
            }
        }));
        imageLoader.Load(imageUrl);
    }

    private void OnImageLoadError(String errorImageUrl, final boolean autoSize)
    {
        if (StringUtils.isEmptyOrNull(errorImageUrl))
        {
            _errorEvent.dispatch(null);
        }
        ImageLoader errorImageLoader = new ImageLoader();
        this._regManager.add(errorImageLoader.addErrorHandler(new SimpleEvent.Handler<Void>() {
            @Override
            public void onFire(Void arg) {
                _errorEvent.dispatch(null);
            }
        }));
        this._regManager.add(errorImageLoader.addLoadHandler(new SimpleEvent.Handler<Image>() {
            @Override
            public void onFire(Image arg) {
                ElementUtils.SetBackgroundImage(_element, arg, autoSize);
                _errorEvent.dispatch(null);
            }
        }));
        errorImageLoader.Load(errorImageUrl);
    }
}
