package com.voidgreen.friendsrelations;

import java.util.ArrayList;

/**
 * Created by yarl on 13.05.16.
 */
public class Album {

    private String id;
    private String name;
    private String coverPhotoUrl;
    private String creationTime;

    private ArrayList<String> photosUrls;

    public Album(String id, String name) {
        this.id = id;
        this.name = name;
        this.photosUrls = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoverPhotoUrl() {
        return coverPhotoUrl;
    }

    public void setCoverPhotoUrl(String coverPhotoUrl) {
        this.coverPhotoUrl = coverPhotoUrl;
    }

    public String getPhotoUrl(int index) {
        return photosUrls.get(index);
    }

    public int getPhotosNumber() {
        return photosUrls.size();
    }

    public void addPhotoUrl(String photosUrl) {
        this.photosUrls.add(photosUrl);
    }

    public ArrayList<String> getPhotosUrls() {
        return photosUrls;
    }
}
