package com.mudasir.moviesappadmin.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Movie {

    private String title;
    private String Description;
    private String thumbnail;
    private String category;
    private String streamingLink;
    private String key;


    public Movie() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Movie(String title, String description, String thumbnail, String category, String streamingLink, String key) {
        this.title = title;
        this.Description = description;
        this.thumbnail = thumbnail;
        this.category = category;
        this.streamingLink = streamingLink;
        this.key=key;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStreamingLink() {
        return streamingLink;
    }

    public void setStreamingLink(String streamingLink) {
        this.streamingLink = streamingLink;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("Description", Description);
        result.put("thumbnail", thumbnail);
        result.put("category", category);
        result.put("streamingLink", streamingLink);
        result.put("key",key);
        return result;
    }

}
