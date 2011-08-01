package com.project.website.shared.contracts.authentication;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.project.website.shared.data.Invitation;
import com.project.website.shared.data.UserProfile;

@RemoteServiceRelativePath("authentication")
public interface AuthenticationService extends RemoteService
{
    void login(String username, String password);

    void logout();

    void register(String email, String password, String name, Invitation invitation);

    UserProfile getUserProfile();

    void invite(String email, String message, String name);
}
