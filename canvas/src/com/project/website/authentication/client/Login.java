package com.project.website.authentication.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.project.website.shared.client.widgets.authentication.LoginWidget;

public class Login implements EntryPoint {

    @Override
    public void onModuleLoad() {
        RootPanel.get("root").add(new LoginWidget());
    }
}
