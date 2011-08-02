package com.project.website.authentication.client;

import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.client.utils.UrlUtils;
import com.project.shared.utils.QueryString;
import com.project.shared.utils.StringUtils;
import com.project.website.shared.client.widgets.authentication.login.LoginWidget;
import com.project.website.shared.client.widgets.authentication.registration.RegistrationRequestData;
import com.project.website.shared.client.widgets.authentication.registration.UserRegistrationViewImpl;
import com.project.website.shared.client.widgets.authentication.resources.AuthenticationResources;
import com.project.website.shared.contracts.authentication.AuthenticationService;
import com.project.website.shared.contracts.authentication.AuthenticationServiceAsync;
import com.project.website.shared.data.QueryParameters;

public class Login implements EntryPoint {

    @Override
    public void onModuleLoad() {
        AuthenticationResources.INSTANCE.main().ensureInjected();
        History.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event)
            {
                handleLoginHistoryEvent(event);
            }
        });
        History.fireCurrentHistoryState();
    }

    protected void handleLoginHistoryEvent(ValueChangeEvent<String> event)
    {
        List<String> inviteIds = Window.Location.getParameterMap().get(QueryParameters.INVITE_ID);
        String inviteId = null;
        if ((null != inviteIds) && (1 == inviteIds.size())) {
            inviteId = URL.decode(inviteIds.get(0));
        }
        else {
            QueryString queryString = QueryString.parse(UrlUtils.getUrlEncoder(), event.getValue());
            inviteId = queryString.get(QueryParameters.INVITE_ID);
        }

        if (false == StringUtils.isWhitespaceOrNull(inviteId)) {
            showRegistration(inviteId);
        }
        else {
            showLogin();
        }
    }

    private void showLogin()
    {
        RootPanel.get("root").clear();
        RootPanel.get("root").add(new LoginWidget());
    }

    private void showRegistration(String inviteId)
    {
        // TODO: save invitation id
        final UserRegistrationViewImpl regWidget = new UserRegistrationViewImpl(inviteId);
        regWidget.addRegistrationRequestHandler(new Handler<RegistrationRequestData>() {
            @Override
            public void onFire(final RegistrationRequestData arg)
            {
                final AuthenticationServiceAsync service =
                        (AuthenticationServiceAsync)GWT.create(AuthenticationService.class);
                final String email = arg.getEmail();
                service.register(email, arg.getPassword(), arg.getName(), arg.getInvitation(), new AsyncCallback<Void>() {

                    @Override
                    public void onSuccess(Void result)
                    {
                        performPostRegistrationLogin(arg);
                    }

                    @Override
                    public void onFailure(Throwable caught)
                    {
                        if (caught instanceof AuthenticationService.UserAlreadyExists)
                        {
                            regWidget.onEmailAlreadyExists(email);
                        }
                        regWidget.setErrorText("Registration failed :(");
                        Window.alert("Unable to register. Reason: " + caught.toString());
                    }
                });
            }
        });
        RootPanel.get("root").clear();
        RootPanel.get("root").add(regWidget);
    }

    private void performPostRegistrationLogin(final RegistrationRequestData arg)
    {
        final AuthenticationServiceAsync service =
                (AuthenticationServiceAsync)GWT.create(AuthenticationService.class);

        service.login(arg.getEmail(), arg.getPassword(), new AsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result)
            {
                // TODO Auto-generated method stub
                LoginWidget.onLoginSuccess();
            }

            @Override
            public void onFailure(Throwable caught)
            {
                // TODO Auto-generated method stub
                Window.alert("Unable to login after registration. Reason: " + caught.toString());
            }
        });
    }
}
