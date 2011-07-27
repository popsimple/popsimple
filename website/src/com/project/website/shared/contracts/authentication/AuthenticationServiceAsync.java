package com.project.website.shared.contracts.authentication;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.project.website.shared.data.Invitation;

public interface AuthenticationServiceAsync {

    void login(String username, String password, AsyncCallback<Void> callback);

    void logout(AsyncCallback<Void> callback);

    void register(String email, String password, String name, Invitation invitation, AsyncCallback<Void> callback);

    void canRegisterUsers(AsyncCallback<Boolean> callback);

    void invite(String email, String message, String name, AsyncCallback<Void> asyncCallback);
}
