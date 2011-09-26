package com.project.website.canvas.client.canvastools.map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.project.website.canvas.client.resources.CanvasResources;

public class MapToolBar extends Composite
{

    private static MapToolBarUiBinder uiBinder = GWT.create(MapToolBarUiBinder.class);

    interface MapToolBarUiBinder extends UiBinder<Widget, MapToolBar>
    {}


    @UiField
    Anchor optionsLink;
    @UiField
    Anchor removeMarkersLink;

    @UiField
    FlowPanel optionsBar;

    @UiField
    TextBox mapSearchTextBox;
    @UiField
    Button mapSearchButton;


    public MapToolBar()
    {
        initWidget(uiBinder.createAndBindUi(this));
        this.optionsLink.addStyleName(CanvasResources.INSTANCE.main().disabledLink());

    }


    public Anchor getOptionsLink()
    {
        return optionsLink;
    }


    public Anchor getRemoveMarkersLink()
    {
        return removeMarkersLink;
    }


    public FlowPanel getOptionsBar()
    {
        return optionsBar;
    }


    public TextBox getMapSearchTextBox()
    {
        return mapSearchTextBox;
    }


    public Button getMapSearchButton()
    {
        return mapSearchButton;
    }


}
