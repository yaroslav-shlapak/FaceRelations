package com.voidgreen.friendsrelations;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by yarl on 13.05.16.
 */
public class Album <V extends Comparable<? super V>>  implements Comparable<V> {

    private String id;
    private String name;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    private int count;


    public String getCreated_time() {
        return created_time;
    }

    public void setCreated_time(String created_time) {
        this.created_time = created_time;
    }

    private String created_time;

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    private String coverUrl;

    private ArrayList<String> photosUrls;

    public Album(String id, String name, String created_time, int count) {
        this.id = id;
        this.name = name;
        this.created_time = created_time;
        this.count = count;
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

    public String getPhotoUrl(int index) {
        return photosUrls.get(index);
    }

    public int getPhotosNumber() {
        return photosUrls.size();
    }

    public void addPhotoUrl(String photosUrl) {
        if(this.photosUrls == null) {
            this.photosUrls = new ArrayList<>();
        }
        this.photosUrls.add(photosUrl);
    }

    public ArrayList<String> getPhotosUrls() {
        return photosUrls;
    }

    @Override
    public int compareTo(V another) {
        return another.compareTo((V) created_time);
    }
}
