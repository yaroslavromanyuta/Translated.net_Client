package com.yaroslav.translatednet_client;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface Api {
    @GET("/get?q=!&langpair=")
    public Call<ServerResponse> getTranslation (@Query("q") String translateFrom,
                                    @Query("langpair") String langPair);


}
