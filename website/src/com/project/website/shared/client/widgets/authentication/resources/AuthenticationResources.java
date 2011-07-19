package com.project.website.shared.client.widgets.authentication.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.project.website.canvas.client.resources.CanvasResources;

public interface AuthenticationResources extends ClientBundle
{
    public static final AuthenticationResources INSTANCE = GWT.create(AuthenticationResources.class);

    @Source("com/project/website/shared/client/resources/images/bitmaps/icecandy-orange.png")
    ImageResource iceCandyOrange();

    Authentication authentication();



}
