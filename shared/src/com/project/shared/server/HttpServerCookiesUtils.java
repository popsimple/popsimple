package com.project.shared.server;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HttpServerCookiesUtils
{
    public static final String GLOBAL_COOKIE_PATH = "/";

    public static Cookie getCookie(HttpServletRequest httpRequest, String cookieName)
    {
        Cookie[] cookies = httpRequest.getCookies();
        if (null == cookies)
        {
            return null;
        }
        for (Cookie cookie : cookies)
        {
            if (cookie.getName().equals(cookieName))
            {
                return cookie;
            }
        }
        return null;
    }

    public static void removeGlobalCookie(HttpServletRequest request, HttpServletResponse response, String cookieName)
    {
        HttpServerCookiesUtils.removeCookie(request, response, cookieName, GLOBAL_COOKIE_PATH);
    }

    public static void removeCookie(HttpServletRequest request, HttpServletResponse response,
            String cookieName, String cookiePath)
    {
        Cookie cookie = HttpServerCookiesUtils.getCookie(request, cookieName);
        if (null == cookie)
        {
            return;
        }
        cookie.setMaxAge(0);
        cookie.setPath(cookiePath);
        response.addCookie(cookie);
    }

    public static void setGlobalCookie(HttpServletResponse response, String cookieName,
            String cookieValue, int ageInSeconds)
    {
        HttpServerCookiesUtils.setCookie(response, cookieName, cookieValue, ageInSeconds, GLOBAL_COOKIE_PATH);
    }

    public static void setCookie(HttpServletResponse response, String cookieName, String cookieValue,
            int ageInSeconds, String path)
    {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setPath(path);
        cookie.setMaxAge(ageInSeconds);
        response.addCookie(cookie);
    }
}
