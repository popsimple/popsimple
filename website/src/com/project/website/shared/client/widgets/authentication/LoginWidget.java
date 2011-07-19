package com.project.website.shared.client.widgets.authentication;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.utils.StringUtils;
import com.project.website.shared.contracts.authentication.AuthenticationService;
import com.project.website.shared.contracts.authentication.AuthenticationServiceAsync;
import com.project.website.shared.data.QueryParameters;

public class LoginWidget extends Composite {

    private static LoginWidgetUiBinder uiBinder = GWT.create(LoginWidgetUiBinder.class);

    interface LoginWidgetUiBinder extends UiBinder<Widget, LoginWidget> {
    }

    @UiField
    FormPanel loginForm;

    @UiField
    TextBox textEmail;

    @UiField
    TextBox textPassword;

    @UiField
    Button buttonLogin;

    @UiField
    HTMLPanel errorPanel;

    @UiField
    Anchor anchorForgot;

    @UiField
    Label errorLabel;

    public LoginWidget() {
        initWidget(uiBinder.createAndBindUi(this));

        this.buttonLogin.setStylePrimaryName("gwt-Button");

        this.clearError();

        this.registerFormHandlers();

        this.anchorForgot.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (false == anchorForgot.getHref().startsWith("http")) {
                    anchorForgot.setText("Bummer, eat more Ginkgo biloba");
                    anchorForgot.setHref("http://en.wikipedia.org/wiki/Ginkgo_biloba");
                    anchorForgot.setTarget("_blank");
                    event.preventDefault();
                }
            }
        });
    }

    private void registerFormHandlers() {

        //NOTE: Due to a bug in GWT we need to manually handle the submit click otherwise
        //NOTE: it throws an exception that the gwt module might need to be recompiled.
        //NOTE: refer to http://code.google.com/p/google-web-toolkit/issues/detail?id=5067
        this.buttonLogin.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                event.preventDefault();
                loginForm.submit();
            }
        });

        this.loginForm.addSubmitHandler(new SubmitHandler() {
            @Override
            public void onSubmit(SubmitEvent event) {
                event.cancel();
                submitLogin();
            }
        });
    }

    private void setLoginControlsEnabled(boolean enabled)
    {
        textEmail.setEnabled(enabled);
        textPassword.setEnabled(enabled);
        buttonLogin.setEnabled(enabled);
    }

    private void submitLogin()
    {
        this.setLoginControlsEnabled(false);
        this.clearError();

        final AuthenticationServiceAsync service =
            (AuthenticationServiceAsync)GWT.create(AuthenticationService.class);
        service.login(this.textEmail.getText(), this.textPassword.getText(), new AsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                String redirectUrl = Window.Location.getParameter(QueryParameters.REDIRECT_URL);
                if (StringUtils.isEmptyOrNull(redirectUrl))
                {
                    //We need to keep the current query string coz it contains debug information.
                    Window.Location.assign(
                            Window.Location.createUrlBuilder().setPath("").buildString());
                }
                else
                {
                    Window.Location.assign(redirectUrl);
                }
            }
            @Override
            public void onFailure(Throwable caught) {
                onLoginFailed();
            }
        });
    }

    private void onLoginFailed()
    {
        this.setLoginControlsEnabled(true);

        //TODO: Replace with constants.
        this.displayError("Login failed :(");
        this.textPassword.setText("");
        this.textPassword.setFocus(true);
    }

    private void clearError() {
        this.errorLabel.setText("");
        this.errorPanel.setVisible(false);
    }

    private void displayError(String error) {
        this.errorLabel.setText(error);
        this.errorPanel.setVisible(true);
    }
}
