package com.project.website.authentication.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.project.website.shared.client.widgets.authentication.login.LoginWidget;
import com.project.website.shared.client.widgets.authentication.resources.AuthenticationResources;

public class Login implements EntryPoint {

    @Override
    public void onModuleLoad() {
        RootPanel.get("root").add(new LoginWidget());
        AuthenticationResources.INSTANCE.authentication().ensureInjected();
//        RootPanel.get("root").add(new RegistrationWidget());
    }
}
