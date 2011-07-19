package com.project.shared.server;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.project.shared.utils.StringUtils;

public class HttpServerCookiesUtils
{
    public static final String ROOT_COOKIE_PATH = "/";

    public static String getCookieValue(HttpServletRequest httpRequest, String cookieName)
    {
        Cookie cookie = HttpServerCookiesUtils.getCookie(httpRequest, cookieName);
        String cookieValue = cookie == null ? null : cookie.getValue();
        return StringUtils.defaultIfEmptyOrNull(cookieValue, null);
    }

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

    public static void removeRootCookie(HttpServletRequest request, HttpServletResponse response, String cookieName)
    {
        HttpServerCookiesUtils.removeCookie(request, response, cookieName, ROOT_COOKIE_PATH);
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

    public static void setRootCookie(HttpServletResponse response, String cookieName,
            String cookieValue, int ageInSeconds)
    {
        HttpServerCookiesUtils.setCookie(response, cookieName, cookieValue, ageInSeconds, ROOT_COOKIE_PATH);
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
