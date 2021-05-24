package com.example.myapplication;

import com.example.myapplication.model.Forecast;
import com.example.myapplication.model.Location;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GitHubService {
    @GET("location/search/?query=Warsaw")
    Call<List<Location>> getWoeid();

    @GET("location/{id}")
    Call<Forecast> getForecast(@Path("id") Integer id);
}
