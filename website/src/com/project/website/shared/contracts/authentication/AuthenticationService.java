package com.project.website.shared.contracts.authentication;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("authentication")
public interface AuthenticationService extends RemoteService
{
    void login(String username, String password);
    void logout();
    void register(String email, String password);
    boolean canRegisterUsers();
}
