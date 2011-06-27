package com.project.authentication.shared;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AuthenticationServiceAsync {

    void login(String username, String password, AsyncCallback<Void> callback);

    void logout(AsyncCallback<Void> callback);
}
