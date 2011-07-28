package com.project.website.shared.server.authentication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.project.shared.utils.StringUtils;
import com.project.shared.utils.UrlUtils;
import com.project.website.shared.data.QueryParameters;

public class AuthenticationFilter implements Filter
{
    public static final String PARAMETER_LOGIN_URL = "loginUrl";
    public static final String PARAMETER_EXCLUDE_PATTERN = "excludePattern";

    @SuppressWarnings("unused")
    private FilterConfig _filterConfig = null;
    private String _loginUrl = "";
    private ArrayList<String> _excludePatternStrings = new ArrayList<String>();
    private Pattern _excludePattern = null;

    @Override
    public void destroy() {
        this._filterConfig = null;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        HttpServletResponse httpResponse = (HttpServletResponse)response;

        if (this.isRequestExcluded(httpRequest))
        {
            chain.doFilter(request, response);
            return;
        }

        if (false == HttpAuthentication.isLoggedIn(httpRequest, httpResponse))
        {
            this.redirectToLogin(httpRequest, httpResponse);
            return;
        }
        chain.doFilter(request, response);
    }

    private void redirectToLogin(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
        throws IOException
    {
        String redirectUrl = UrlUtils.buildUrl(httpRequest.getRequestURI(), httpRequest.getQueryString());
        StringBuilder query = new StringBuilder(httpRequest.getQueryString());
        if (query.length() > 0)
        {
            UrlUtils.appendQueryParameter(query, QueryParameters.REDIRECT_URL, redirectUrl);
        }
        httpResponse.sendRedirect(
                httpResponse.encodeRedirectURL(UrlUtils.buildUrl(this._loginUrl, query.toString())));
    }

    private boolean isRequestExcluded(HttpServletRequest httpRequest)
    {
        return this._excludePattern.matcher(httpRequest.getRequestURI()).matches();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this._filterConfig = filterConfig;

        this._loginUrl = filterConfig.getInitParameter(PARAMETER_LOGIN_URL);

        for (String pattern : filterConfig.getInitParameter(PARAMETER_EXCLUDE_PATTERN).split(","))
        {
            this._excludePatternStrings.add(pattern.trim());
        }
        this._excludePattern = Pattern.compile(StringUtils.join("|", this._excludePatternStrings), Pattern.CASE_INSENSITIVE);
    }

}
