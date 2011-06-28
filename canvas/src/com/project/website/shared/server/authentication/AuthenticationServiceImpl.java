package com.project.website.shared.server.authentication;

import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.code.twig.ObjectDatastore;
import com.google.code.twig.annotation.AnnotationObjectDatastore;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
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
    public void login(String username, String password)
    {
        this.logout();

        ObjectDatastore datastore = new AnnotationObjectDatastore();
        User user = datastore.load(User.class, username);
        if (null == user)
        {
            //TODO: replace with proper exception.
            throw new RuntimeException("Error");
//            user = new User();
//            user.username = username;
//            user.password = password;
//            datastore.store(user);
        }
        if (false == user.password.equals(password))
        {
          //TODO: replace with proper exception.
            throw new RuntimeException("Error");
        }

        UUID sessionId = UUID.randomUUID();
        Cookie cookie = new Cookie(AUTHENTICATION_COOKIE_NAME, sessionId.toString());
        cookie.setPath("/");
        cookie.setMaxAge(COOKIE_MAX_AGE);

        this.getThreadLocalResponse().addCookie(cookie);
    }

    @Override
    public void logout() {
        HttpServletRequest request = this.getThreadLocalRequest();
        if (null == request)
        {
            return;
        }
        this.removeCookie(this.getThreadLocalRequest(),
                this.getThreadLocalResponse(), AUTHENTICATION_COOKIE_NAME);
    }

    public boolean isLoggedIn(HttpServletRequest httpRequest){
        if (null == httpRequest)
        {
            return false;
        }
        Cookie cookie = this.getCookie(httpRequest.getCookies(), AUTHENTICATION_COOKIE_NAME);
        if ((null == cookie) || (StringUtils.isEmptyOrNull(cookie.getValue())))
        {
            return false;
        }
        return true;
    }

    //TODO: move to CookiesUtils
    public Cookie getCookie(Cookie[] cookies, String cookieName)
    {
        for (Cookie cookie : cookies)
        {
            if (cookie.getName().equals(cookieName))
            {
                return cookie;
            }
        }
        return null;
    }

    //TODO: move to CookiesUtils
    public void removeCookie(HttpServletRequest request, HttpServletResponse response, String cookieName)
    {
        Cookie cookie = this.getCookie(request.getCookies(), cookieName);
        if (null == cookie)
        {
            return;
        }
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
