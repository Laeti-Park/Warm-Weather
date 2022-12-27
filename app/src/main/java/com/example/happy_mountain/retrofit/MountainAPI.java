package com.example.happy_mountain.retrofit;

import com.example.happy_mountain.item.MountainItem;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface MountainAPI {
    @GET("v2/local/search/keyword.json")
    @NotNull
    Call<MountainItem> getSearchKeyword(
            @Header("Authorization") @NotNull String key,
            @Query("query") @NotNull String query,
            @Query("category_group_code") String category,
            @Query("x") String x,
            @Query("y") String y,
            @Query("radius") Integer radius,
            @Query("sort") String sort
    );
}