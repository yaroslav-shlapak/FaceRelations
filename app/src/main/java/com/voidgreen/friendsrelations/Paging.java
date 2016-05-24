package com.voidgreen.friendsrelations;

/**
 * Created by yaroslav on 25.05.16.
 */
public class Paging {
    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    private String previous;
    private String next;
}
