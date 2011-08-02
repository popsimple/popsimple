package com.project.website.shared.client.widgets.authentication.registration;

import com.google.gwt.event.shared.HandlerRegistration;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.client.interfaces.HasErrorText;

public interface UserRegistrationView extends HasErrorText
{
    HandlerRegistration addRegistrationRequestHandler(Handler<RegistrationRequestData> handler);

    void onEmailAlreadyExists(String alreadyTakenEmail);
}