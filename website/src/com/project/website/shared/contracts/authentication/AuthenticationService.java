package com.project.website.shared.contracts.authentication;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.project.website.shared.data.Invitation;

@RemoteServiceRelativePath("authentication")
public interface AuthenticationService extends RemoteService
{
    void login(String username, String password);
    void logout();
    void register(String email, String password, String name, Invitation invitation);
    boolean canRegisterUsers();
    void invite(String email, String message, String name);
}
