package com.example.happy_mountain.retrofit;

import com.example.happy_mountain.item.AreaItem;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface AreaAPI {
    @GET("v2/local/search/address.json")
    @NotNull
    Call<AreaItem> getSearchKeyword(
            @Header("Authorization") @NotNull String key,
            @Query("query") @NotNull String query,
            @Query("analyze_type") String analyzeType
    );
}