package com.project.website.shared.server.authentication;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.code.twig.ObjectDatastore;
import com.google.code.twig.annotation.AnnotationObjectDatastore;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.project.shared.server.HttpServerCookiesUtils;
import com.project.shared.utils.StringUtils;
import com.project.website.shared.contracts.authentication.AuthenticationService;
import com.project.website.shared.data.User;

public class AuthenticationServiceImpl extends RemoteServiceServlet implements AuthenticationService
{
    private static final long serialVersionUID = 1L;

    private static final String AUTHENTICATION_COOKIE_NAME = "authenticationCookie";
    private static final int COOKIE_EXPIRATION_DAYS = 7;
    private static final int COOKIE_MAX_AGE = COOKIE_EXPIRATION_DAYS * 24 * 60 * 60;

    @Override
    public void login(String username, String password) throws IOException
    {
        this.logout();

        if (StringUtils.isEmptyOrNull(username))
        {
            this.onLoginFailed();
            return;
        }

        ObjectDatastore datastore = new AnnotationObjectDatastore();
        User user = datastore.load(User.class, username);
        if (null == user)
        {
            this.onLoginFailed();
            return;
//            user = new User();
//            user.username = username;
//            user.password = password;
//            datastore.store(user);
        }
        if (false == user.password.equals(password))
        {
            this.onLoginFailed();
            return;
        }

        UUID sessionId = UUID.randomUUID();
        HttpServerCookiesUtils.setGlobalCookie(this.getThreadLocalResponse(),
                AUTHENTICATION_COOKIE_NAME, sessionId.toString(), COOKIE_MAX_AGE);
    }

    private void onLoginFailed() throws IOException
    {
        this.getThreadLocalResponse().sendError(HttpServletResponse.SC_UNAUTHORIZED);
        return;
    }

    @Override
    public void logout() {
        HttpServletRequest request = this.getThreadLocalRequest();
        if (null == request)
        {
            return;
        }
        HttpServerCookiesUtils.removeGlobalCookie(this.getThreadLocalRequest(),
                this.getThreadLocalResponse(), AUTHENTICATION_COOKIE_NAME);
    }

    public boolean isLoggedIn(HttpServletRequest httpRequest){
        if (null == httpRequest)
        {
            return false;
        }
        Cookie cookie = HttpServerCookiesUtils.getCookie(httpRequest, AUTHENTICATION_COOKIE_NAME);
        if ((null == cookie) || (StringUtils.isEmptyOrNull(cookie.getValue())))
        {
            return false;
        }
        return true;
    }
}
