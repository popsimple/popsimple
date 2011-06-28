package com.project.website.authentication.server;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AuthenticationFilter implements Filter
{
    public static final String PARAMETER_LOGIN_URL = "loginUrl";
    public static final String PARAMETER_EXCLUDE_PATTERN = "excludePattern";
    public static final String QUERY_PARAM_REDIRECT_URL = "redirectUrl";
    public static final String SESSION_ATTRIBUTE_IS_AUTHENTICATED = "isAuthenticated";

    private FilterConfig _filterConfig = null;
    private String _loginUrl = "";
    private String _excludePattern = "";

    @Override
    public void destroy() {
        this._filterConfig = null;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest)request;

        if (this.isRequestExcluded(httpRequest))
        {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);
        if (false == this.isAuthenticated(session))
        {
            this.redirectToLogin(httpRequest, (HttpServletResponse)response);
            return;
        }
        chain.doFilter(request, response);
    }

    private void redirectToLogin(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
        throws IOException
    {
        String redirectUrl = this.getCompleteRelativeUrl(httpRequest);
        String queryString = httpRequest.getQueryString() + "&" +
            QUERY_PARAM_REDIRECT_URL + "=" + redirectUrl;
        httpResponse.sendRedirect(
                httpResponse.encodeRedirectURL(this.buildUrl(this._loginUrl, queryString)));
    }

    private boolean isRequestExcluded(HttpServletRequest httpRequest)
    {
        if (httpRequest.getRequestURI().matches(this._excludePattern))
        {
            return true;
        }
        return false;
    }

    //TODO: Move to Utils
    private String buildUrl(String url, String queryString)
    {
        if (queryString == null) {
            return url;
        }
        return url + "?" + queryString;
    }

    //TODO: Move to Utils
    private String getCompleteRelativeUrl(HttpServletRequest request)
    {
        return this.buildUrl(request.getRequestURI(), request.getQueryString());
    }

    private boolean isAuthenticated(HttpSession session)
    {
        if (null == session)
        {
            return false;
        }
        Boolean isAuthenticated = (Boolean)session.getAttribute(SESSION_ATTRIBUTE_IS_AUTHENTICATED);
        if ((null == isAuthenticated) || (false == isAuthenticated.booleanValue()))
        {
            return false;
        }
        return true;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this._filterConfig = filterConfig;

        this._loginUrl = filterConfig.getInitParameter(PARAMETER_LOGIN_URL);
        this._excludePattern = filterConfig.getInitParameter(PARAMETER_EXCLUDE_PATTERN);
    }

}
