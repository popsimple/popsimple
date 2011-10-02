package com.project.website.canvas.client.canvastools.sitecrop;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.client.handlers.SpecificKeyPressHandler;
import com.project.shared.client.utils.HandlerUtils;
import com.project.website.canvas.client.resources.CanvasResources;
import com.project.website.canvas.client.shared.widgets.ToggleButtonPanel;

public class SiteCropToolbar extends Composite
{
    //#region UiBinder Declarations

	private static SiteCropToolbarUiBinder uiBinder = GWT
			.create(SiteCropToolbarUiBinder.class);

	interface SiteCropToolbarUiBinder extends UiBinder<Widget, SiteCropToolbar> {
	}

	//#endregion

	//#region UiFields

	@UiField
	ToggleButtonPanel toggleButtonPanel;

    @UiField
    CheckBox chkAutoSize;

    @UiField
    ToggleButton moveButton;

    @UiField
    PushButton browseButton;

    @UiField
    ToggleButton cropButton;

    @UiField
    Button acceptCropButton;

    @UiField
    Label urlLabel;

    @UiField
    TextBox urlTextBox;

    @UiField
    CheckBox interactiveCheckBox;

    //#endregion

    private SimpleEvent<String> _urlChangeEvent = new SimpleEvent<String>();

	public SiteCropToolbar() {
		initWidget(uiBinder.createAndBindUi(this));

		this.registerHandlers();

		this.toggleButtonPanel.setDefaultButton(this.moveButton);

		this.browseButton.getUpFace().setImage(
		        new Image(CanvasResources.INSTANCE.cropBrowseIcon()));
	}

	public void registerHandlers()
	{
        this.urlTextBox.addKeyPressHandler(new SpecificKeyPressHandler(KeyCodes.KEY_ENTER) {
            @Override
            public void onSpecificKeyPress(KeyPressEvent event) {
                _urlChangeEvent.dispatch(urlTextBox.getText());
            }
        });
	}

	public HandlerRegistration addToggleMoveModeRequestHandler(final Handler<Boolean> handler)
    {
        return this.moveButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                handler.onFire(event.getValue());
            }
        });
    }

	public HandlerRegistration addToggleCropModeRequestHandler(final Handler<Boolean> handler)
	{
	    return this.cropButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                handler.onFire(event.getValue());
            }
        });
	}

	public HandlerRegistration addUrlChangedHandler(final Handler<String> handler)
	{
	    return this._urlChangeEvent.addHandler(handler);
	}

	public HandlerRegistration addDebugClickRequestHandler(final Handler<Void> handler)
	{
        return this.urlLabel.addClickHandler(HandlerUtils.asClickHandler(handler));
	}

	public HandlerRegistration addAcceptCropRequestHandler(final Handler<Void> handler)
	{
	    return this.acceptCropButton.addClickHandler(HandlerUtils.asClickHandler(handler));
	}

	public void setAcceptCropVisibility(boolean visible)
	{
	    this.acceptCropButton.setVisible(visible);
	}

	public void enableCrop(boolean enable)
	{
	    this.cropButton.setEnabled(enable);
	}

	public void enableBrowse(boolean enable)
	{
	    this.browseButton.setEnabled(enable);
	}

	public HandlerRegistration addBrowseRequestHandler(final Handler<Void> handler)
	{
	    return this.browseButton.addClickHandler(HandlerUtils.asClickHandler(handler));
	}

	public HandlerRegistration addIsInteractiveChangedHandler(ValueChangeHandler<Boolean> handler)
	{
	    return this.interactiveCheckBox.addValueChangeHandler(handler);
	}

	public void toggleMoveMode()
	{
	    this.moveButton.setValue(true, true);
	}

	public void toggleCropMode()
    {
        this.moveButton.setValue(true, true);
    }

	public void setUrl(String url)
	{
	    if (this.urlTextBox.getText().equals(url))
	    {
	        return;
	    }
	    this.urlTextBox.setValue(url);
	    this._urlChangeEvent.dispatch(url);
	}

	public void setIsInteractive(boolean isInteractive)
	{
	    this.interactiveCheckBox.setValue(isInteractive, false);
	}
}
