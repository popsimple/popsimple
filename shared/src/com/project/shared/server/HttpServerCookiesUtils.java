package com.project.shared.server;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Objects;
import com.project.shared.utils.StringUtils;

public class HttpServerCookiesUtils
{
    public static final String ROOT_COOKIE_PATH = "/";

    public static String getCookieValue(HttpServletRequest request, String cookieName)
    {
        return getCookieValue(cookieName, request.getCookies());
    }

    public static Cookie getCookie(HttpServletRequest httpRequest, String cookieName)
    {
        return getCookie(cookieName, httpRequest.getCookies());
    }

    public static String getCookieValue(String cookieName, Cookie[] cookies)
    {
        Cookie cookie = HttpServerCookiesUtils.getCookie(cookieName, cookies);
        String cookieValue = cookie == null ? null : cookie.getValue();
        return StringUtils.defaultIfNullOrEmpty(cookieValue, null);
    }


    public static Cookie getCookie(String cookieName, Cookie[] cookies)
    {
        if (null == cookies)
        {
            return null;
        }
        for (Cookie cookie : cookies)
        {
            if ((null != cookie) && (Objects.equal(cookie.getName(), cookieName)))
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
