package com.example.gsb_visite.data.api;

import com.example.gsb_visite.data.model.Visiteur;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/visiteurs/login")
    Call<Visiteur> login(@Body JsonObject credentials);

    @POST("api/visiteurs/signup")
    Call<Visiteur> signup(@Body JsonObject request);

    @GET("api/visiteurs/me")
    Call<JsonElement> getCurrentVisiteur();

    @GET("api/visiteurs/{id}/portefeuille")
    Call<JsonElement> getPortefeuille(@Path("id") String visiteurId);

    @GET("api/praticiens/{id}")
    Call<JsonElement> getPraticien(@Path("id") String praticienId);
}
