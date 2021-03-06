package com.project.shared.client.utils;

import com.google.gwt.http.client.URL;
import com.google.gwt.regexp.shared.RegExp;
import com.project.shared.data.KeyValue;
import com.project.shared.data.StringEncoder;
import com.project.shared.data.StringKeyValue;

public class UrlUtils {

    public static final String PROTOCOL_DELIMETER = "://";
    public static final String PROTOCOL_HTTP = "http";

    private static RegExp urlValidator = null;
    private static RegExp urlPlusTldValidator = null;

    public static StringEncoder getUrlEncoder()
    {
        return new StringEncoder(){
            @Override
            public String encode(String value)
            {
                return URL.encode(value);
            }

            @Override
            public String decode(String value)
            {
                return URL.decode(value);
            }};
    }

    public static boolean isValidUrl(String url, boolean topLevelDomainRequired) {
        if (urlValidator == null || urlPlusTldValidator == null) {
            urlValidator = RegExp
                    .compile("^((ftp|http|https)://[\\w@.\\-\\_]+(:\\d{1,5})?(/[\\w#!:.?+=&%@!\\_\\-/]+)*){1}$");
            urlPlusTldValidator = RegExp
                    .compile("^((ftp|http|https)://[\\w@.\\-\\_]+\\.[a-zA-Z]{2,}(:\\d{1,5})?(/[\\w#!:.?+=&%@!\\_\\-/]+)*){1}$");
        }
        return (topLevelDomainRequired ? urlPlusTldValidator : urlValidator).exec(url) != null;
    }

    /**
     * Compares two urls by first decoding and trimming them. Note that depending on the server
     * the urls may not really be equivalent (for example if the server treats encodings differently).
     * However for most purposes this is probably a good enough comparison.
     */
    public static boolean areEquivalent(String url1, String url2)
    {
        if ((null == url1) || (null == url2)) {
            return url1 == url2;
        }
        return URL.decode(url1).trim().equals(URL.decode(url2).trim());
    }

    /**
     * Encodes the url but first decodes it to prevent double-encoding of escape characters (such as %)
     */
    public static String encodeOnce(String url) {
        return URL.encode(URL.decode(url));
    }

    public static String buildUrl(String url, String queryString)
    {
        if (queryString == null) {
            return url;
        }
        return url + "?" + queryString;
    }

    public static String buildUrl(String url, StringKeyValue... queryParams)
    {
        StringBuilder query = new StringBuilder();
        for (KeyValue<String, String> queryParam : queryParams)
        {
            appendQueryParameter(query, queryParam.getKey(), queryParam.getValue());
        }
        return UrlUtils.buildUrl(url, query.toString());
    }

    public static void appendQueryParameter(StringBuilder url, String parameterName, String parameterValue)
    {
        String prefix = url.length() > 0 ? "&" : "";
        url.append(prefix + UrlUtils.formatQueryParameter(parameterName, parameterValue));
    }

    public static String formatQueryParameter(String parameterName, String parameterValue)
    {
        return UrlUtils.encodeOnce(parameterName) + "=" + UrlUtils.encodeOnce(parameterValue);
    }

    public static String ensureProtocol(String url)
    {
        //TODO: Replace with a proper trim method which also handles unicode whitespace (Guava?)
        url = url.trim();
        int delimeterIndex = url.indexOf(PROTOCOL_DELIMETER);
        if (-1 == delimeterIndex)
        {
            return PROTOCOL_HTTP + PROTOCOL_DELIMETER + url;
        }
        if (0 == delimeterIndex)
        {
            return PROTOCOL_HTTP + url;
        }
        return url;
    }
}
