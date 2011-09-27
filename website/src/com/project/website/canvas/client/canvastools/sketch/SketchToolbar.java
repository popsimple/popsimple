package com.project.website.canvas.client.canvastools.sketch;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class SketchToolbar extends Composite
{

    private static SketchToolbarUiBinder uiBinder = GWT.create(SketchToolbarUiBinder.class);

    interface SketchToolbarUiBinder extends UiBinder<Widget, SketchToolbar>
    {}

    public SketchToolbar()
    {
        initWidget(uiBinder.createAndBindUi(this));
    }

}
