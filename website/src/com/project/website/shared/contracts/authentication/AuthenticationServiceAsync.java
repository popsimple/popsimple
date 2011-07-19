package com.project.website.shared.contracts.authentication;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AuthenticationServiceAsync {

    void login(String username, String password, AsyncCallback<Void> callback);

    void logout(AsyncCallback<Void> callback);

    void register(String email, String password, String name, AsyncCallback<Void> callback);

    void canRegisterUsers(AsyncCallback<Boolean> callback);
}
