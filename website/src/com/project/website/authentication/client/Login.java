package com.project.website.authentication.client;

import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.project.website.shared.client.widgets.authentication.login.LoginWidget;
import com.project.website.shared.client.widgets.authentication.resources.AuthenticationResources;
import com.project.website.shared.data.UrlParameterConstants;

public class Login implements EntryPoint {

    @Override
    public void onModuleLoad() {
        RootPanel.get("root").add(new LoginWidget());
        AuthenticationResources.INSTANCE.main().ensureInjected();
//        RootPanel.get("root").add(new RegistrationWidget());
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
        List<String> inviteIds = Window.Location.getParameterMap().get(UrlParameterConstants.URL_PARAMETER_INVITE_ID);
        if ((null != inviteIds) && (1 == inviteIds.size())) {
            // TODO: save invitation id
        }
    }
}
