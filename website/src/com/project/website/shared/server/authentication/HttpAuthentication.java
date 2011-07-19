package com.project.website.shared.server.authentication;

import java.security.MessageDigest;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.project.shared.server.HttpServerCookiesUtils;
import com.project.website.shared.data.User;

public class HttpAuthentication
{

    private static final String AUTHENTICATION_COOKIE_NAME = "authenticationCookie";
    private static final int COOKIE_EXPIRATION_DAYS = 7;
    private static final int COOKIE_MAX_AGE = COOKIE_EXPIRATION_DAYS * 24 * 60 * 60;
    private static final String SESSION_COOKIE_NAME = "sessionCookie";
    private static final String USERNAME_COOKIE_NAME = "userNameCookie";


    public static User getAuthenticatedUser(HttpServletRequest httpRequest, HttpServletResponse response)
    {
        String userName = HttpServerCookiesUtils.getCookieValue(httpRequest, USERNAME_COOKIE_NAME);
        String userHash = HttpServerCookiesUtils.getCookieValue(httpRequest, AUTHENTICATION_COOKIE_NAME);

        boolean valid = false;
        User user = null;

        if ((null != userName) && (null != userHash)) {
            user = AuthenticationUtils.loadUser(userName);
            valid = getUserHash(user, httpRequest, response).equals(userHash);
        }

        if (valid) {
            return user;
        }

        // Don't keep invalid cookies
        clearAuthCookies(httpRequest, response);
        return null;
    }

    public static boolean isLoggedIn(HttpServletRequest httpRequest, HttpServletResponse response)
    {
        return null != getAuthenticatedUser(httpRequest, response);
    }

    private static String assignNewSessionCookie(HttpServletResponse response)
    {
        UUID sessionId = UUID.randomUUID();
        String sessionCookie = sessionId.toString();
        HttpServerCookiesUtils.setRootCookie(response, SESSION_COOKIE_NAME, sessionCookie, COOKIE_MAX_AGE);
        return sessionCookie;
    }

    private static String getSessionCookie(HttpServletRequest request)
    {
        return HttpServerCookiesUtils.getCookieValue(request, SESSION_COOKIE_NAME);
    }

    public static void setAuthCookies(User user, HttpServletRequest request, HttpServletResponse response)
    {
        String userHash = getUserHash(user, request, assignNewSessionCookie(response));
        HttpServerCookiesUtils.setRootCookie(response, AUTHENTICATION_COOKIE_NAME, userHash, COOKIE_MAX_AGE);
        HttpServerCookiesUtils.setRootCookie(response, USERNAME_COOKIE_NAME, user.username, COOKIE_MAX_AGE);
    }


    private static String getUserHash(User user, HttpServletRequest request, HttpServletResponse response)
    {
        return getUserHash(user, request, getSessionCookie(request));
    }

    private static String getUserHash(User user, HttpServletRequest request, String sessionStr)
    {
        MessageDigest m = AuthenticationUtils.newDigest();
        m.update(user.username.getBytes());
        m.update(user.password.getBytes());
        m.update(sessionStr.getBytes());
        m.update(request.getRemoteAddr().getBytes());
        return AuthenticationUtils.getDigestString(m);
    }

    public static void clearAuthCookies(HttpServletRequest request, HttpServletResponse response)
    {
        HttpServerCookiesUtils.removeRootCookie(request, response, AUTHENTICATION_COOKIE_NAME);
        HttpServerCookiesUtils.removeRootCookie(request, response, USERNAME_COOKIE_NAME);
        assignNewSessionCookie(response);
    }

}
