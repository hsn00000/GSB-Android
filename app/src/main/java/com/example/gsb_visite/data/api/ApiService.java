package com.example.gsb_visite.data.api;

import com.example.gsb_visite.data.model.LoginCredentials;
import com.example.gsb_visite.data.model.LoginRequest;
import com.example.gsb_visite.data.model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/visiteurs/login")
    Call<LoginResponse> login(@Body LoginCredentials credentials);

    @POST("api/visiteurs/signup")
    Call<LoginResponse> signup(@Body LoginRequest request);
}
