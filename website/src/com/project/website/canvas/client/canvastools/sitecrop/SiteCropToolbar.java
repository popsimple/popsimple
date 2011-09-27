package com.project.website.canvas.client.canvastools.sitecrop;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class SiteCropToolbar extends Composite
{
	private static SiteCropToolbarUiBinder uiBinder = GWT
			.create(SiteCropToolbarUiBinder.class);

	interface SiteCropToolbarUiBinder extends UiBinder<Widget, SiteCropToolbar> {
	}
	
    @UiField
    CheckBox chkAutoSize;

    @UiField
    ToggleButton moveButton;

    @UiField
    ToggleButton browseButton;

    @UiField
    ToggleButton cropButton;

    @UiField
    Button acceptCropButton;

    @UiField
    Label urlLabel;
    
    @UiField
    TextBox urlTextBox;

	public SiteCropToolbar() {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.acceptCropButton.setVisible(false);
	}
	
	public ToggleButton getMoveButton()
	{
		return this.moveButton;
	}
	
	public ToggleButton getBrowseButton()
	{
		return this.browseButton;
	}
	
	public ToggleButton getCropButton()
	{
		return this.cropButton;
	}
	
	public TextBox getUrlTextBox()
	{
		return this.urlTextBox;
	}
	
	public Label getUrlLabel()
	{
		return this.urlLabel;
	}
	
	public Button getAcceptCropButton()
	{
		return this.acceptCropButton;
	}
}
