package com.project.authentication.server;

import javax.servlet.http.HttpSession;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.project.authentication.shared.AuthenticationService;

public class AuthenticationServiceImpl extends RemoteServiceServlet implements AuthenticationService
{
    private static final long serialVersionUID = 1L;

    @Override
    public void login(String username, String password)
    {
        if (username.isEmpty())
        {
            throw new RuntimeException("Error");
        }
        HttpSession session = this.getThreadLocalRequest().getSession(true);
        if (null == session)
        {
            return;
        }
        session.setAttribute(AuthenticationFilter.SESSION_ATTRIBUTE_IS_AUTHENTICATED, true);
    }

    @Override
    public void logout() {
        HttpSession session = this.getThreadLocalRequest().getSession(false);
        if (null == session)
        {
            return;
        }
        session.removeAttribute(AuthenticationFilter.SESSION_ATTRIBUTE_IS_AUTHENTICATED);
    }
}
