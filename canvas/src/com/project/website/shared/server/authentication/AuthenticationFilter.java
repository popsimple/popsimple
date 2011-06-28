package com.project.website.shared.server.authentication;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.project.shared.utils.StringUtils;

public class AuthenticationFilter implements Filter
{
    public static final String PARAMETER_LOGIN_URL = "loginUrl";
    public static final String PARAMETER_EXCLUDE_PATTERN = "excludePattern";
    public static final String QUERY_PARAM_REDIRECT_URL = "redirectUrl";

    @SuppressWarnings("unused")
    private FilterConfig _filterConfig = null;
    private String _loginUrl = "";
    private ArrayList<String> _excludePatterns = new ArrayList<String>();

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

        if (false == new AuthenticationServiceImpl().isLoggedIn(httpRequest))
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
        for (String excludePattern : this._excludePatterns)
        {
            if (httpRequest.getRequestURI().matches(excludePattern))
            {
                return true;
            }
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

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this._filterConfig = filterConfig;

        this._loginUrl = filterConfig.getInitParameter(PARAMETER_LOGIN_URL);

        for (String pattern : filterConfig.getInitParameter(PARAMETER_EXCLUDE_PATTERN).split(","))
        {
            this._excludePatterns.add(pattern.trim());
        }
    }

}
