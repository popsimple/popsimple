package com.project.website.shared.client.widgets.authentication;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.handlers.SpecificKeyPressHandler;
import com.project.shared.utils.StringUtils;
import com.project.website.shared.contracts.authentication.AuthenticationService;
import com.project.website.shared.contracts.authentication.AuthenticationServiceAsync;
import com.project.website.shared.data.QueryParameters;
import com.project.website.shared.server.authentication.AuthenticationFilter;

public class LoginWidget extends Composite {

    private static LoginWidgetUiBinder uiBinder = GWT.create(LoginWidgetUiBinder.class);

    interface LoginWidgetUiBinder extends UiBinder<Widget, LoginWidget> {
    }

    @UiField
    FocusPanel focusPanel;

    @UiField
    TextBox textEmail;

    @UiField
    TextBox textPassword;

    @UiField
    Button buttonLogin;

    @UiField
    HTMLPanel errorPanel;

    public LoginWidget() {
        initWidget(uiBinder.createAndBindUi(this));

        this.errorPanel.setVisible(false);

        this.focusPanel.addDomHandler(new SpecificKeyPressHandler(KeyCodes.KEY_ENTER) {
            @Override
            public void onSpecificKeyPress(KeyPressEvent event) {
                submitLogin();
            }
        }, KeyPressEvent.getType());

        this.buttonLogin.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                submitLogin();
            }
        });
    }

    private void submitLogin()
    {
        AuthenticationServiceAsync service =
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
        this.errorPanel.setVisible(true);
        this.textPassword.setText("");
    }

}
