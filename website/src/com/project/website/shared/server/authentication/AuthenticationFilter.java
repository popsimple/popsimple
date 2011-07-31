package com.project.website.shared.server.authentication;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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

import com.project.shared.server.UrlEncodedQueryString;
import com.project.shared.utils.StringUtils;
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
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException
    {
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
        StringBuffer redirectUrlBuffer = httpRequest.getRequestURL();
        String requestQueryString = httpRequest.getQueryString();
        String fullRequestURL = redirectUrlBuffer.toString();

        UrlEncodedQueryString responseUrlQueryString = UrlEncodedQueryString.create();
        if (false == StringUtils.isWhitespaceOrNull(requestQueryString)) {
            fullRequestURL = redirectUrlBuffer.append("?" + requestQueryString).toString();

            UrlEncodedQueryString requestUrlQueryString = UrlEncodedQueryString.parse(requestQueryString);

            if (requestUrlQueryString.contains(QueryParameters.GWT_CODESERVER)) {
                responseUrlQueryString.append(QueryParameters.GWT_CODESERVER, requestUrlQueryString.get(QueryParameters.GWT_CODESERVER));
            }
        }

        responseUrlQueryString.set(QueryParameters.REDIRECT_URL, fullRequestURL);
        // TODO there must be a more sane way to build urls...
        URI loginURI;
        String localNameAndPath = "//" + httpRequest.getServerName() + ":" + httpRequest.getServerPort() + this._loginUrl;
        try {
            loginURI = new URI(httpRequest.getScheme(), localNameAndPath, null);
        } catch (URISyntaxException e) {
            // TODO WTF to do? for now catching and re-throwing as runtime exception
            throw new RuntimeException(e);
        }
        String redirectUrlWithQuery = responseUrlQueryString.apply(loginURI).toURL().toString();
        httpResponse.sendRedirect(httpResponse.encodeRedirectURL(redirectUrlWithQuery));
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
