package com.project.authentication.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.project.authentication.shared.AuthenticationService;
import com.project.authentication.shared.AuthenticationServiceAsync;

public class LoginWidget extends Composite {

    private static LoginWidgetUiBinder uiBinder = GWT.create(LoginWidgetUiBinder.class);

    interface LoginWidgetUiBinder extends UiBinder<Widget, LoginWidget> {
    }

    @UiField
    FormPanel formPanel;

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

        this.textEmail.addKeyDownHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event) {
                if (KeyCodes.KEY_ENTER == event.getNativeKeyCode())
                {
                    submitLogin();
                }
            }
        });
        this.textPassword.addKeyDownHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event) {
                if (KeyCodes.KEY_ENTER == event.getNativeKeyCode())
                {
                    submitLogin();
                }
            }
        });
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
                String redirectUrl = Window.Location.getParameter("redirectUrl");
                if (redirectUrl.isEmpty())
                {
                    return;
                }
                Window.Location.assign(redirectUrl);
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
