package com.project.website.canvas.client.textparsers;

import com.google.gwt.regexp.shared.RegExp;

public class UrlTextParser implements TextParser {
    public enum UrlType {
        Unknown, Video, Image, WebPage,
    };

    protected final RegExp URL_YOUTUBE = RegExp
            .compile("^http:\\/\\/(?:www\\.)?youtube.com\\/watch\\?(?=.*v=\\w+)(?:\\S+)?$");

    protected UrlType urlType;

    private String embeddableUrl;

    public UrlType getUrlType() {
        return this.urlType;
    }

    public String getEmbeddableUrl() {
        return this.embeddableUrl;
    }

    public boolean parse(String text) {
        if (URL_YOUTUBE.test(text)) {
            this.urlType = UrlType.Video;
//            MatchResult match = URL_YOUTUBE.exec(text);
            // this.embeddableUrl = match.getGroup(index)
        }
        return false;
    }
}
