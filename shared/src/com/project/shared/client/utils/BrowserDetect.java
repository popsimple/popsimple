package com.project.shared.client.utils;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Heuristically detects the browser.
 * Thanks to http://www.quirksmode.org/js/detect.html
 */
public final class BrowserDetect extends JavaScriptObject
{
    private static BrowserDetect INSTANCE;

    protected BrowserDetect() {}

    public static BrowserDetect get() {
        if (null == BrowserDetect.INSTANCE) {
            BrowserDetect.INSTANCE = BrowserDetect.create();
        }
        return BrowserDetect.INSTANCE;
    }

    public final native String getBrowser() /*-{
        return this.browser;
    }-*/;

    public final native String getVersion() /*-{
        return this.version;
    }-*/;

    public final native String getOS() /*-{
        return this.OS;
    }-*/;

    private final static native BrowserDetect create() /*-{
        var BrowserDetect = {
            init: function () {
                this.browser = this.searchString(this.dataBrowser) || "unknown";
                this.version = this.searchVersion($wnd.navigator.userAgent)
                    || this.searchVersion($wnd.navigator.appVersion)
                    || "unknown";
                this.OS = this.searchString(this.dataOS) || "unknown";
            },
            searchString: function (data) {
                for (var i=0;i<data.length;i++) {
                    var dataString = data[i].string;
                    var dataProp = data[i].prop;
                    this.versionSearchString = data[i].versionSearch || data[i].identity;
                    if (dataString) {
                        if (dataString.indexOf(data[i].subString) !== -1) {
                            return data[i].identity;
                        }
                    }
                    else if (dataProp) {
                        return data[i].identity;
                    }
                }
            },
            searchVersion: function (dataString) {
                var index = dataString.indexOf(this.versionSearchString);
                if (index === -1) {
                    return;
                }
                return parseFloat(dataString.substring(index+this.versionSearchString.length+1));
            },
            dataBrowser: [
                {
                    string: $wnd.navigator.userAgent,
                    subString: "Chrome",
                    identity: "Chrome"
                },
                {   string: $wnd.navigator.userAgent,
                    subString: "OmniWeb",
                    versionSearch: "OmniWeb/",
                    identity: "OmniWeb"
                },
                {
                    string: $wnd.navigator.vendor,
                    subString: "Apple",
                    identity: "Safari",
                    versionSearch: "Version"
                },
                {
                    prop: window.opera,
                    identity: "Opera",
                    versionSearch: "Version"
                },
                {
                    string: $wnd.navigator.vendor,
                    subString: "iCab",
                    identity: "iCab"
                },
                {
                    string: $wnd.navigator.vendor,
                    subString: "KDE",
                    identity: "Konqueror"
                },
                {
                    string: $wnd.navigator.userAgent,
                    subString: "Firefox",
                    identity: "Firefox"
                },
                {
                    string: $wnd.navigator.vendor,
                    subString: "Camino",
                    identity: "Camino"
                },
                {       // for newer Netscapes (6+)
                    string: $wnd.navigator.userAgent,
                    subString: "Netscape",
                    identity: "Netscape"
                },
                {
                    string: $wnd.navigator.userAgent,
                    subString: "MSIE",
                    identity: "Explorer",
                    versionSearch: "MSIE"
                },
                {
                    string: $wnd.navigator.userAgent,
                    subString: "Gecko",
                    identity: "Mozilla",
                    versionSearch: "rv"
                },
                {       // for older Netscapes (4-)
                    string: $wnd.navigator.userAgent,
                    subString: "Mozilla",
                    identity: "Netscape",
                    versionSearch: "Mozilla"
                }
            ],
            dataOS : [
                {
                    string: $wnd.navigator.platform,
                    subString: "Win",
                    identity: "Windows"
                },
                {
                    string: $wnd.navigator.platform,
                    subString: "Mac",
                    identity: "Mac"
                },
                {
                       string: $wnd.navigator.userAgent,
                       subString: "iPhone",
                       identity: "iPhone/iPod"
                },
                {
                    string: $wnd.navigator.platform,
                    subString: "Linux",
                    identity: "Linux"
                },
                {
                       string: $wnd.navigator.userAgent,
                       subString: "Android",
                       identity: "Android"
                }
            ]

        };
        BrowserDetect.init();
        return BrowserDetect;
    }-*/;

}