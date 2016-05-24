package com.voidgreen.friendsrelations;

import retrofit.Callback;
import retrofit.http.GET;

public interface Api {
    @GET("/")
    public void getData(Callback<Page> response);

}
