package com.project.canvas.client.shared.widgets.media;

import java.util.List;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.client.shared.searchProviders.interfaces.MediaInfo;
import com.project.canvas.client.shared.searchProviders.interfaces.MediaSearchProvider;

public interface IMediaSearchPanel<TMediaInformation>
extends IsWidget, TakesValue<TMediaInformation>
{
    HandlerRegistration addMediaSelectedHandler(SimpleEvent.Handler<TMediaInformation> handler);

    void setSearchProviders(List<? extends MediaSearchProvider> searchProviders);
}
