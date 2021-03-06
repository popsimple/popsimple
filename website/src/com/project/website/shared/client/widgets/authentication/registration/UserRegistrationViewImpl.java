package com.project.website.shared.client.widgets.authentication.registration;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.website.shared.data.Invitation;

public class UserRegistrationViewImpl extends Composite implements UserRegistrationView {

    private static RegistrationWidgetUiBinder uiBinder = GWT.create(RegistrationWidgetUiBinder.class);

    interface RegistrationWidgetUiBinder extends UiBinder<Widget, UserRegistrationViewImpl> {
    }

    @UiField
    Label genericErrorLabel;

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

    private SimpleEvent<RegistrationRequestData> registrationRequestEvent = new SimpleEvent<RegistrationRequestData>();

    private String _inviteId;

    public UserRegistrationViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));

        this.registerFormHandlers();

        this.buttonRegister.setStylePrimaryName("gwt-Button");

        this.setAutoComplete(this.textPassword, false);
        this.setAutoComplete(this.textConfirmPassword, false);

    }

    public UserRegistrationViewImpl(String inviteId)
    {
        this();
        this._inviteId = inviteId;
    }

    @Override
    public HandlerRegistration addRegistrationRequestHandler(Handler<RegistrationRequestData> handler) {
        return this.registrationRequestEvent.addHandler(handler);
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
                submitRegistration(textEmail.getText(), textPassword.getText(), textName.getText());
            }
        });
    }

    private void submitRegistration(String email, String password, String name) {
        if (false == this.validateFields()) {
            return;
        }
        Invitation invitation = new Invitation();
        invitation.id = this._inviteId;
        registrationRequestEvent.dispatch(new RegistrationRequestData(name, email, password, invitation));
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
        if (false == this.textName.getText().matches(".{2,}"))
        {
            isValid = false;
            this.setError(nameErrorLabel, "Name must be at least two characters long");
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

    @Override
    public void setErrorText(String text)
    {
        this.setError(this.genericErrorLabel, text);
    }

    @Override
    public void clearErrorText()
    {
        this.clearError(this.genericErrorLabel);
    }

    @Override
    public void onEmailAlreadyExists(String alreadyTakenEmail)
    {
        this.setError(this.emailErrorLabel, alreadyTakenEmail + " is already registered.");
    }
}
