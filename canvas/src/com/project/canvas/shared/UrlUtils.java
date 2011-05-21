package com.project.canvas.shared;

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

}
