package com.project.website.authentication.shared;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("authentication")
public interface AuthenticationService extends RemoteService
{
    void login(String username, String password);
    void logout();
}
