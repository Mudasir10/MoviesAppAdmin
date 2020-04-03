package com.mudasir.moviesappadmin.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class MovieTrailor {

    private String title;
    private String thumbnail;
    private String streamingLink;
    private String key;

    public MovieTrailor() {
    }

    public MovieTrailor(String title, String thumbnail, String streamingLink, String key) {
        this.title = title;
        this.thumbnail = thumbnail;
        this.streamingLink = streamingLink;
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getStreamingLink() {
        return streamingLink;
    }

    public void setStreamingLink(String streamingLink) {
        this.streamingLink = streamingLink;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("thumbnail", thumbnail);
        result.put("streamingLink", streamingLink);
        result.put("key",key);
        return result;
    }
}
