package com.voidgreen.friendsrelations;

import java.util.List;

public class Page {
    private List<Album> data;

    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }

    public List<Album> getData() {
        return data;
    }

    public void setData(List<Album> data) {
        this.data = data;
    }

    private Paging paging;
}
