package com.project.website.shared.client.widgets.registration;

import javax.swing.RootPaneContainer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.project.shared.client.utils.TextBoxUtils;

public class RegistrationWidget extends Composite {

    private static RegistrationWidgetUiBinder uiBinder = GWT.create(RegistrationWidgetUiBinder.class);

    interface RegistrationWidgetUiBinder extends UiBinder<Widget, RegistrationWidget> {
    }

    @UiField
    TextBox textName;

    @UiField
    Label nameErrorLabel;

    @UiField
    TextBox textEmail;

    @UiField
    Label emailErrorLabel;

    @UiField
    TextBox textPassword;

    @UiField
    Label passwordErrorLabel;

    @UiField
    TextBox textConfirmPassword;

    @UiField
    Label confirmErrorLabel;

    @UiField
    Button buttonRegister;

    @UiField
    FormPanel registrationForm;



    public RegistrationWidget() {
        initWidget(uiBinder.createAndBindUi(this));

        this.registerFormHandlers();

        this.buttonRegister.setStylePrimaryName("gwt-Button");

        this.setAutoComplete(this.textPassword, false);
        this.setAutoComplete(this.textConfirmPassword, false);

    }

    //TODO: Move to Utils.
    private void setAutoComplete(TextBox textBox, boolean autoComplete) {
        if (autoComplete)
        {
            textBox.getElement().setAttribute("autocomplete", "on");
        }
        else {
            textBox.getElement().setAttribute("autocomplete", "off");
        }
    }

    private void registerFormHandlers() {

        //NOTE: Due to a bug in GWT we need to manually handle the submit click otherwise
        //NOTE: it throws an exception that the gwt module might need to be recompiled.
        //NOTE: refer to http://code.google.com/p/google-web-toolkit/issues/detail?id=5067
        this.buttonRegister.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                event.preventDefault();
                registrationForm.submit();
            }
        });

        this.registrationForm.addSubmitHandler(new SubmitHandler() {
            @Override
            public void onSubmit(SubmitEvent event) {
                event.cancel();
                submitRegistration();
            }
        });
    }

    private void submitRegistration() {
        this.validateFields();
    }


    private void clearError(Label label){
        label.setText("");
        label.setVisible(false);
    }

    private void setError(Label label, String error){
        label.setText(error);
        label.setVisible(true);
    }

    //TODO: use some kind of validator.
    private boolean validateFields()
    {
        boolean isValid = true;

        this.clearError(nameErrorLabel);
        if (false == this.textName.getText().matches(".+"))
        {
            isValid = false;
            this.setError(nameErrorLabel, "Name cannot be empty");
        }
        this.clearError(emailErrorLabel);
        if (false == this.textEmail.getText().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$"))
        {
            isValid = false;
            this.setError(emailErrorLabel, "Invalid email address");
        }
        this.clearError(passwordErrorLabel);
        if (false == this.textPassword.getText().matches(".{6,}"))
        {
            isValid = false;
            this.setError(passwordErrorLabel, "Password must be at least 6 characters long");
        }
        this.clearError(confirmErrorLabel);
        if (false == this.textConfirmPassword.getText().matches(this.textPassword.getText()))
        {
            isValid = false;
            this.setError(confirmErrorLabel, "Confirm password does not match");
        }
        return isValid;
    }
}
