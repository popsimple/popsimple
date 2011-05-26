package com.project.canvas.shared;

import com.google.gwt.http.client.URL;
import com.google.gwt.regexp.shared.RegExp;

public class UrlUtils {
    private static RegExp urlValidator = null;
    private static RegExp urlPlusTldValidator = null;

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
        return URL.decode(url1).trim().equals(URL.decode(url2).trim());
    }

    /**
     * Encodes the url but first decodes it to prevent double-encoding of escape characters (such as %)
     */
    public static String encodeOnce(String url) {
        return URL.encode(URL.decode(url));
    }
}
